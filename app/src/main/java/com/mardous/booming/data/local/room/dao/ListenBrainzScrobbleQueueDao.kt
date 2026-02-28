package com.mardous.booming.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mardous.booming.data.local.room.entity.ListenBrainzScrobbleQueueEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para la cola de scrobbles de ListenBrainz
 */
@Dao
interface ListenBrainzScrobbleQueueDao {
    
    /**
     * Obtiene todos los scrobbles pendientes como Flow
     */
    @Query("SELECT * FROM listenbrainz_scrobble_queue ORDER BY createdAt ASC")
    fun getAllPending(): Flow<List<ListenBrainzScrobbleQueueEntity>>
    
    /**
     * Obtiene todos los scrobbles pendientes (sync)
     */
    @Query("SELECT * FROM listenbrainz_scrobble_queue ORDER BY createdAt ASC")
    suspend fun getAllPendingSync(): List<ListenBrainzScrobbleQueueEntity>
    
    /**
     * Cuenta los scrobbles pendientes
     */
    @Query("SELECT COUNT(*) FROM listenbrainz_scrobble_queue")
    suspend fun getPendingCount(): Int
    
    /**
     * Agrega un scrobble a la cola
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scrobble: ListenBrainzScrobbleQueueEntity): Long
    
    /**
     * Agrega múltiples scrobbles a la cola
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(scrobbles: List<ListenBrainzScrobbleQueueEntity>)
    
    /**
     * Elimina un scrobble de la cola (ya fue enviado)
     */
    @Delete
    suspend fun delete(scrobble: ListenBrainzScrobbleQueueEntity)
    
    /**
     * Elimina un scrobble por ID
     */
    @Query("DELETE FROM listenbrainz_scrobble_queue WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    /**
     * Incrementa el contador de reintentos
     */
    @Query("UPDATE listenbrainz_scrobble_queue SET retryCount = retryCount + 1 WHERE id = :id")
    suspend fun incrementRetryCount(id: Long)
    
    /**
     * Obtiene scrobbles con más de N reintentos
     */
    @Query("SELECT * FROM listenbrainz_scrobble_queue WHERE retryCount >= :maxRetries")
    suspend fun getWithMaxRetries(maxRetries: Int): List<ListenBrainzScrobbleQueueEntity>
    
    /**
     * Limpia la cola (después de logout o sync exitoso)
     */
    @Query("DELETE FROM listenbrainz_scrobble_queue")
    suspend fun clearAll()
    
    /**
     * Obtiene scrobbles antiguos para eliminar
     */
    @Query("SELECT * FROM listenbrainz_scrobble_queue WHERE createdAt < :timestamp")
    suspend fun getOlderThan(timestamp: Long): List<ListenBrainzScrobbleQueueEntity>
}
