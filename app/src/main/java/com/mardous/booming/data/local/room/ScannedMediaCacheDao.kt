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

package com.mardous.booming.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScannedMediaCacheDao {

    /**
     * Obtiene todos los archivos de audio cacheados válidos.
     */
    @Query("SELECT * FROM scanned_media_cache WHERE isValid = 1 ORDER BY title ASC")
    fun getAllCachedMedia(): Flow<List<ScannedMediaCache>>

    /**
     * Obtiene todos los archivos de audio cacheados válidos como lista.
     */
    @Query("SELECT * FROM scanned_media_cache WHERE isValid = 1 ORDER BY title ASC")
    suspend fun getAllCachedMediaList(): List<ScannedMediaCache>

    /**
     * Busca un archivo por su path completo.
     */
    @Query("SELECT * FROM scanned_media_cache WHERE filePath = :path LIMIT 1")
    suspend fun getByPath(path: String): ScannedMediaCache?

    /**
     * Busca un archivo por su path completo como Flow.
     */
    @Query("SELECT * FROM scanned_media_cache WHERE filePath = :path LIMIT 1")
    fun getByPathFlow(path: String): Flow<ScannedMediaCache?>

    /**
     * Busca por ID de MediaStore (para compatibilidad).
     */
    @Query("SELECT * FROM scanned_media_cache WHERE mediaStoreId = :mediaStoreId LIMIT 1")
    suspend fun getByMediaStoreId(mediaStoreId: Long): ScannedMediaCache?

    /**
     * Obtiene archivos modificados desde un timestamp.
     * Útil para escaneo incremental.
     */
    @Query("SELECT * FROM scanned_media_cache WHERE lastModified > :timestamp AND isValid = 1")
    suspend fun getModifiedSince(timestamp: Long): List<ScannedMediaCache>

    /**
     * Busca archivos por título, artista o álbum.
     */
    @Query("""
        SELECT * FROM scanned_media_cache 
        WHERE isValid = 1 AND (
            title LIKE '%' || :query || '%' OR 
            artist LIKE '%' || :query || '%' OR 
            album LIKE '%' || :query || '%'
        )
        ORDER BY title ASC
    """)
    fun search(query: String): Flow<List<ScannedMediaCache>>

    /**
     * Obtiene conteo de archivos válidos.
     */
    @Query("SELECT COUNT(*) FROM scanned_media_cache WHERE isValid = 1")
    suspend fun getValidCount(): Int

    /**
     * Inserta o actualiza un archivo cacheado.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(media: ScannedMediaCache): Long

    /**
     * Inserta o actualiza múltiples archivos cacheados.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(media: List<ScannedMediaCache>)

    /**
     * Actualiza un archivo cacheado existente.
     */
    @Update
    suspend fun update(media: ScannedMediaCache)

    /**
     * Marca un archivo como inválido (eliminado o corrupto).
     */
    @Query("UPDATE scanned_media_cache SET isValid = 0 WHERE filePath = :path")
    suspend fun invalidate(path: String)

    /**
     * Elimina permanentemente un archivo del cache.
     */
    @Query("DELETE FROM scanned_media_cache WHERE filePath = :path")
    suspend fun delete(path: String)

    /**
     * Elimina todos los archivos inválidos antiguos.
     * Útil para limpieza periódica.
     */
    @Query("DELETE FROM scanned_media_cache WHERE isValid = 0 AND scanTimestamp < :olderThan")
    suspend fun purgeInvalid(olderThan: Long = System.currentTimeMillis() - 604800000) // 7 días por defecto

    /**
     * Limpia todo el cache.
     * Usar solo en casos extremos.
     */
    @Query("DELETE FROM scanned_media_cache")
    suspend fun clearAll()

    /**
     * Obtiene todos los paths válidos (para verificar archivos eliminados).
     */
    @Query("SELECT filePath FROM scanned_media_cache WHERE isValid = 1")
    suspend fun getAllValidPaths(): List<String>

    /**
     * Invalida múltiples archivos por sus paths.
     */
    @Query("UPDATE scanned_media_cache SET isValid = 0 WHERE filePath IN (:paths)")
    suspend fun invalidatePaths(paths: List<String>)
}
