/*
 * Copyright (c) 2024 Christians Martínez Alvarado
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mardous.booming.data.scanner

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.mardous.booming.data.local.room.ScannedMediaCache
import com.mardous.booming.data.local.room.ScannedMediaCacheDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

/**
 * Estado del scanner.
 */
sealed class ScanState {
    object Idle : ScanState()
    object Scanning : ScanState()
    data class Progress(val current: Int, val total: Int, val fileName: String) : ScanState()
    data class Complete(val added: Int, val updated: Int, val removed: Int) : ScanState()
    data class Error(val message: String) : ScanState()
}

/**
 * Manager principal para escaneo de biblioteca de música.
 * Orquesta el escaneo de carpetas, extracción de metadatos y cacheo en Room.
 */
class MediaScannerManager(
    private val cacheDao: ScannedMediaCacheDao
) : KoinComponent {

    private val context: Context by inject()
    private val fileScanner: FileScanner by inject()
    private val folderManager: FolderSelectionManager by inject()

    companion object {
        private const val TAG = "MediaScannerManager"
        const val WORK_NAME_PERIODIC = "media_scanner_periodic"
        const val WORK_NAME_ONETIME = "media_scanner_onetime"
        
        // Configuración de escaneo - Constants para evitar hardcoded values
        private const val CACHE_CLEANUP_DAYS = 7
        private const val SCAN_INTERVAL_HOURS = 6L
    }

    // Estado del scanner
    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    // Flow con todos los archivos cacheados
    val cachedMedia = cacheDao.getAllCachedMedia()

    /**
     * Escanea todas las carpetas habilitadas.
     */
    suspend fun scanAllFolders(): Result<ScanCompleteInfo> = withContext(Dispatchers.IO) {
        _scanState.value = ScanState.Scanning

        try {
            val folders = folderManager.getEnabledFolders()
            var totalAdded = 0
            var totalUpdated = 0
            var totalRemoved = 0

            for (folder in folders) {
                val result = scanFolder(folder)
                totalAdded += result.added
                totalUpdated += result.updated
                totalRemoved += result.removed
            }

            // Limpiar entradas inválidas
            cleanupInvalidEntries()

            val completeInfo = ScanCompleteInfo(totalAdded, totalUpdated, totalRemoved)
            _scanState.value = ScanState.Complete(totalAdded, totalUpdated, totalRemoved)

            Result.success(completeInfo)

        } catch (e: Exception) {
            Log.e(TAG, "Scan failed", e)
            val errorState = ScanState.Error(e.message ?: "Unknown error")
            _scanState.value = errorState
            Result.failure(e)
        }
    }

    /**
     * Escanea una carpeta específica.
     */
    private suspend fun scanFolder(folder: ScanFolder): ScanFolderResult = withContext(Dispatchers.IO) {
        var added = 0
        var updated = 0
        var removed = 0

        val audioFiles = if (folder.uri != null) {
            // Carpeta SAF
            val uri = Uri.parse(folder.uri)
            fileScanner.scanDocumentDirectory(uri) { current, total, name ->
                _scanState.value = ScanState.Progress(current, total, name)
            }
        } else {
            // Carpeta regular
            val directory = java.io.File(folder.path)
            if (directory.exists() && directory.canRead()) {
                fileScanner.scanDirectory(directory) { current, total, name ->
                    _scanState.value = ScanState.Progress(current, total, name)
                }
            } else {
                emptyList()
            }
        }

        // Obtener paths conocidos en cache para esta carpeta
        val allCachedPaths = cacheDao.getAllValidPaths()
        val folderPathPrefix = folder.path
        val cachedInThisFolder = allCachedPaths.filter { it.startsWith(folderPathPrefix) }

        // Procesar cada archivo escaneado
        audioFiles.forEach { audioInfo ->
            val existing = cacheDao.getByPath(audioInfo.filePath)

            val needsUpdate = existing == null ||
                audioInfo.lastModified > existing.lastModified ||
                audioInfo.size != existing.fileSize

            if (needsUpdate) {
                val cacheEntry = ScannedMediaCache(
                    filePath = audioInfo.filePath,
                    fileName = audioInfo.fileName,
                    fileSize = audioInfo.size,
                    lastModified = audioInfo.lastModified,
                    title = audioInfo.title ?: audioInfo.fileName,
                    artist = audioInfo.artist,
                    album = audioInfo.album,
                    albumArtist = audioInfo.albumArtist,
                    genre = audioInfo.genre,
                    year = audioInfo.year,
                    trackNumber = audioInfo.trackNumber,
                    duration = audioInfo.duration,
                    bitrate = audioInfo.bitrate,
                    sampleRate = audioInfo.sampleRate,
                    scanTimestamp = System.currentTimeMillis(),
                    mediaStoreId = null, // Puede vincularse después si se encuentra en MediaStore
                    isValid = true
                )

                cacheDao.insert(cacheEntry)
                if (existing == null) added++ else updated++
            }
        }

        // Marcar archivos eliminados (están en cache pero no en el escaneo actual)
        val scannedPaths = audioFiles.map { it.filePath }
        val deletedPaths = cachedInThisFolder.filter { it !in scannedPaths }
        
        if (deletedPaths.isNotEmpty()) {
            cacheDao.invalidatePaths(deletedPaths)
            removed = deletedPaths.size
        }

        ScanFolderResult(added, updated, removed)
    }

    /**
     * Limpia entradas inválidas antiguas.
     */
    private suspend fun cleanupInvalidEntries() {
        // Eliminar entradas que no han sido actualizadas en CACHE_CLEANUP_DAYS días
        val cleanupTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(CACHE_CLEANUP_DAYS)
        cacheDao.purgeInvalid(cleanupTime)
    }

    /**
     * Programa escaneo periódico con WorkManager.
     * Se ejecuta cada SCAN_INTERVAL_HOURS horas cuando el dispositivo está cargando.
     */
    fun schedulePeriodicScan() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<MediaScanWorker>(
            SCAN_INTERVAL_HOURS, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME_PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        Log.d(TAG, "Periodic scan scheduled")
    }

    /**
     * Cancela escaneo periódico.
     */
    fun cancelPeriodicScan() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_PERIODIC)
        Log.d(TAG, "Periodic scan cancelled")
    }

    /**
     * Ejecuta escaneo inmediato.
     */
    fun scanNow() {
        val workRequest = OneTimeWorkRequestBuilder<MediaScanWorker>()
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
        Log.d(TAG, "One-time scan requested")
    }

    /**
     * Obtiene conteo de archivos cacheados válidos.
     */
    suspend fun getCachedCount(): Int {
        return cacheDao.getValidCount()
    }

    /**
     * Limpia todo el cache.
     */
    suspend fun clearCache() {
        cacheDao.clearAll()
        Log.d(TAG, "Cache cleared")
    }

    /**
     * Busca archivos en cache por query.
     */
    fun search(query: String) = cacheDao.search(query)
}

/**
 * Resultado del escaneo completo.
 */
data class ScanCompleteInfo(
    val added: Int,
    val updated: Int,
    val removed: Int
)

/**
 * Resultado del escaneo de una carpeta.
 */
data class ScanFolderResult(
    val added: Int,
    val updated: Int,
    val removed: Int
)

/**
 * Worker para escaneo en background.
 */
class MediaScanWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val scannerManager: MediaScannerManager by inject()

    override suspend fun doWork(): Result {
        return try {
            val result = scannerManager.scanAllFolders()
            if (result.isSuccess) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("MediaScanWorker", "Scan failed", e)
            Result.retry()
        }
    }
}
