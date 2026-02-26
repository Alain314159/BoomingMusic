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

package com.mardous.booming.data.local.repository

import android.content.Context
import android.util.Log
import com.mardous.booming.data.local.room.BoomingDatabase
import com.mardous.booming.data.local.room.ScannedMediaCache
import com.mardous.booming.data.local.room.ScannedMediaCacheDao
import com.mardous.booming.data.mapper.toSong
import com.mardous.booming.data.model.Song
import com.mardous.booming.data.scanner.MediaScannerManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Repository híbrido que combina:
 * 1. Cache del scanner independiente (nuevo)
 * 2. MediaStore (existente, como fallback)
 *
 * Esto permite una transición gradual sin romper funcionalidad existente.
 */
class MediaRepository : KoinComponent {

    private val context: Context by inject()
    private val database: BoomingDatabase by inject()
    private val cacheDao: ScannedMediaCacheDao = database.scannedMediaCacheDao()
    private val songRepository: SongRepository by inject()
    private val scannerManager: MediaScannerManager by inject()

    companion object {
        private const val TAG = "MediaRepository"
    }

    /**
     * Obtiene todas las canciones.
     * Primero intenta desde el cache del scanner, fallback a MediaStore.
     */
    suspend fun getAllSongs(): List<Song> {
        return try {
            // Intentar desde cache del scanner
            val cached = cacheDao.getAllCachedMediaList()
            if (cached.isNotEmpty()) {
                cached.mapNotNull { it.toSong() }
            } else {
                // Fallback a MediaStore
                songRepository.songs()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting songs, falling back to MediaStore", e)
            songRepository.songs()
        }
    }

    /**
     * Obtiene canciones como Flow para observación reactiva.
     */
    fun getAllSongsFlow(): Flow<List<Song>> {
        return cacheDao.getAllCachedMedia().map { cachedList ->
            if (cachedList.isNotEmpty()) {
                cachedList.mapNotNull { it.toSong() }
            } else {
                songRepository.songs()
            }
        }
    }

    /**
     * Busca canciones por query.
     */
    fun searchSongs(query: String): Flow<List<Song>> {
        return cacheDao.search(query).map { cachedList ->
            if (cachedList.isNotEmpty()) {
                cachedList.mapNotNull { it.toSong() }
            } else {
                songRepository.songs(query)
            }
        }
    }

    /**
     * Obtiene canción por ID de MediaStore.
     */
    fun getSongById(songId: Long): Song {
        return songRepository.song(songId)
    }

    /**
     * Obtiene canción por path de archivo.
     * Usa el cache primero para mayor rapidez.
     */
    suspend fun getSongByPath(filePath: String): Song? {
        return try {
            // Primero buscar en cache
            val cached = cacheDao.getByPath(filePath)
            if (cached != null && cached.isValid) {
                cached.toSong()
            } else {
                // Fallback a MediaStore
                songRepository.songByFilePath(filePath, ignoreBlacklist = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting song by path: $filePath", e)
            songRepository.songByFilePath(filePath, ignoreBlacklist = true)
        }
    }

    /**
     * Obtiene conteo de canciones en cache.
     */
    suspend fun getCachedCount(): Int {
        return cacheDao.getValidCount()
    }

    /**
     * Obtiene conteo de canciones en MediaStore.
     */
    suspend fun getMediaStoreCount(): Int {
        return songRepository.songs().size
    }

    /**
     * Inicia escaneo manual de la biblioteca.
     */
    suspend fun refreshLibrary(): Result<com.mardous.booming.data.scanner.ScanCompleteInfo> {
        return scannerManager.scanAllFolders()
    }

    /**
     * Programa escaneo automático periódico.
     */
    fun scheduleAutoScan() {
        scannerManager.schedulePeriodicScan()
    }

    /**
     * Cancela escaneo automático.
     */
    fun cancelAutoScan() {
        scannerManager.cancelPeriodicScan()
    }

    /**
     * Obtiene el estado del scanner.
     */
    fun getScanState() = scannerManager.scanState

    /**
     * Obtiene medios cacheados como Flow.
     */
    fun getCachedMediaFlow() = scannerManager.cachedMedia

    /**
     * Limpia el cache del scanner.
     */
    suspend fun clearCache() {
        cacheDao.clearAll()
        Log.d(TAG, "Media cache cleared")
    }

    /**
     * Verifica si el cache está habilitado y tiene datos.
     */
    suspend fun isCacheEnabled(): Boolean {
        return cacheDao.getValidCount() > 0
    }
}

/**
 * Extiende ScannedMediaCache para convertir a Song.
 */
fun ScannedMediaCache.toSong(): Song? {
    // Validar que tengamos al menos título o filePath
    if (title.isNullOrBlank() && fileName.isBlank()) {
        return null
    }

    return Song(
        id = mediaStoreId ?: 0,  // Si no hay MediaStore ID, usar 0
        data = filePath,
        title = title ?: fileName,
        trackNumber = trackNumber ?: 0,
        year = year ?: 0,
        size = fileSize,
        duration = duration ?: 0,
        dateAdded = scanTimestamp / 1000,
        dateModified = lastModified / 1000,
        albumId = 0,  // No disponible en cache
        albumName = album ?: "",
        artistId = 0,  // No disponible en cache
        artistName = artist ?: "",
        albumArtist = albumArtist,
        genreName = genre
    )
}
