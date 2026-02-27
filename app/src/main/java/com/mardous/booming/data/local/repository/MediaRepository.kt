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
import com.mardous.booming.data.local.room.ScannedMediaCacheDao
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
 *
 * @property context Application context
 * @property cacheDao DAO para acceso al cache de archivos escaneados
 * @property songRepository Repository de canciones MediaStore (fallback)
 * @property scannerManager Manager para escaneo de archivos en background
 *
 * @see MediaScannerManager
 * @see ScannedMediaCache
 */
class MediaRepository(
    private val cacheDao: ScannedMediaCacheDao
) : KoinComponent {

    private val context: Context by inject()
    private val repository: Repository by inject()
    private val scannerManager: MediaScannerManager by inject()

    companion object {
        private const val TAG = "MediaRepository"
    }

    /**
     * Obtiene todas las canciones.
     * Primero intenta desde el cache del scanner, fallback a MediaStore.
     *
     * @return Lista de canciones ordenadas por título
     */
    suspend fun getAllSongs(): List<Song> {
        return try {
            // Intentar desde cache del scanner
            val cached = cacheDao.getAllCachedMediaList()
            if (cached.isNotEmpty()) {
                cached.mapNotNull { it.toSong() }
            } else {
                // Fallback a MediaStore
                repository.allSongs()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting songs, falling back to MediaStore", e)
            repository.allSongs()
        }
    }

    /**
     * Obtiene canciones como Flow para observación reactiva.
     *
     * @return Flow de lista de canciones
     */
    fun getAllSongsFlow(): Flow<List<Song>> {
        return cacheDao.getAllCachedMedia().map { cachedList ->
            if (cachedList.isNotEmpty()) {
                cachedList.mapNotNull { it.toSong() }
            } else {
                repository.allSongs()
            }
        }
    }

    /**
     * Busca canciones por query de búsqueda.
     *
     * @param query Texto a buscar en título, artista o álbum
     * @return Flow de canciones que coinciden con la búsqueda
     */
    fun searchSongs(query: String): Flow<List<Song>> {
        return cacheDao.search(query).map { cachedList ->
            if (cachedList.isNotEmpty()) {
                cachedList.mapNotNull { it.toSong() }
            } else {
                repository.searchSongs(query)
            }
        }
    }

    /**
     * Obtiene canción por ID de MediaStore.
     *
     * @param songId ID de la canción en MediaStore
     * @return Song o null si no existe
     */
    fun getSongById(songId: Long): Song {
        return repository.songById(songId)
    }

    /**
     * Obtiene canción por path de archivo.
     * Usa el cache primero para mayor rapidez.
     *
     * @param filePath Path completo del archivo
     * @return Song o null si no existe
     */
    suspend fun getSongByPath(filePath: String): Song? {
        return try {
            // Primero buscar en cache
            val cached = cacheDao.getByPath(filePath)
            if (cached != null && cached.isValid) {
                cached.toSong()
            } else {
                // Fallback a MediaStore
                repository.songByFilePath(filePath, ignoreBlacklist = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting song by path: $filePath", e)
            repository.songByFilePath(filePath, ignoreBlacklist = true)
        }
    }

    /**
     * Obtiene conteo de canciones en cache.
     *
     * @return Número de canciones cacheadas válidas
     */
    suspend fun getCachedCount(): Int {
        return cacheDao.getValidCount()
    }

    /**
     * Obtiene conteo de canciones en MediaStore.
     *
     * @return Número de canciones en MediaStore
     */
    suspend fun getMediaStoreCount(): Int {
        return repository.allSongs().size
    }

    /**
     * Inicia escaneo manual de la biblioteca.
     * Escanea todas las carpetas habilitadas.
     *
     * @return Result con información del escaneo o error
     */
    suspend fun refreshLibrary(): Result<com.mardous.booming.data.scanner.ScanCompleteInfo> {
        return scannerManager.scanAllFolders()
    }

    /**
     * Programa escaneo automático periódico.
     * Se ejecuta cada 6 horas cuando el dispositivo está cargando.
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
     * Obtiene el estado actual del scanner.
     *
     * @return StateFlow con el estado del escaneo
     */
    fun getScanState() = scannerManager.scanState

    /**
     * Obtiene medios cacheados como Flow.
     *
     * @return Flow de lista de ScannedMediaCache
     */
    fun getCachedMediaFlow() = scannerManager.cachedMedia

    /**
     * Limpia el cache del scanner.
     * Elimina todas las entradas cacheadas.
     */
    suspend fun clearCache() {
        cacheDao.clearAll()
        Log.d(TAG, "Media cache cleared")
    }

    /**
     * Verifica si el cache está habilitado y tiene datos.
     *
     * @return true si hay al menos una canción cacheada
     */
    suspend fun isCacheEnabled(): Boolean {
        return cacheDao.getValidCount() > 0
    }

    /**
     * Obtiene estadísticas completas de la biblioteca musical.
     *
     * @return LibraryStats con información detallada de la biblioteca
     */
    suspend fun getLibraryStats(): LibraryStats {
        val songs = getAllSongs()
        val albums = repository.allAlbums()
        val artists = repository.allArtists()

        val totalDuration = songs.sumOf { it.duration }
        val averageDuration = songs.map { it.duration }.average().toLong()
        val genreCounts = songs.groupBy { it.genreName?.takeIf { g -> g.isNotBlank() } }
            .filterKeys { it != null }
            .mapValues { it.value.size }
        val mostCommonGenre = genreCounts.maxByOrNull { it.value }?.key

        val yearRange = songs.mapNotNull { it.year.takeIf { y -> y > 0 } }.let { years ->
            if (years.isNotEmpty()) years.minOrNull() to years.maxOrNull()
            else null to null
        }

        return LibraryStats(
            totalSongs = songs.size,
            totalAlbums = albums.size,
            totalArtists = artists.size,
            totalDuration = totalDuration,
            averageSongDuration = averageDuration,
            mostCommonGenre = mostCommonGenre,
            yearRange = yearRange,
            totalSizeBytes = songs.sumOf { it.size }
        )
    }
}

/**
 * Estadísticas completas de la biblioteca musical.
 *
 * @property totalSongs Número total de canciones
 * @property totalAlbums Número total de álbumes
 * @property totalArtists Número total de artistas
 * @property totalDuration Duración total en milisegundos
 * @property averageSongDuration Duración promedio por canción
 * @property mostCommonGenre Género más común
 * @property yearRange Rango de años (mínimo, máximo)
 * @property totalSizeBytes Tamaño total en bytes
 */
data class LibraryStats(
    val totalSongs: Int,
    val totalAlbums: Int,
    val totalArtists: Int,
    val totalDuration: Long,
    val averageSongDuration: Long,
    val mostCommonGenre: String?,
    val yearRange: Pair<Int?, Int?>,
    val totalSizeBytes: Long
)

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
        rawDateModified = lastModified / 1000,
        albumId = 0,  // No disponible en cache
        albumName = album ?: "",
        artistId = 0,  // No disponible en cache
        artistName = artist ?: "",
        albumArtistName = albumArtist,
        genreName = genre
    )
}
