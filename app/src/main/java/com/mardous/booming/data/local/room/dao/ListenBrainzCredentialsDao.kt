package com.mardous.booming.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mardous.booming.data.local.room.entity.ListenBrainzCredentialsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para credenciales de ListenBrainz
 */
@Dao
interface ListenBrainzCredentialsDao {
    
    /**
     * Obtiene las credenciales como Flow
     */
    @Query("SELECT * FROM listenbrainz_credentials WHERE id = 1")
    fun getCredentialsFlow(): Flow<ListenBrainzCredentialsEntity?>
    
    /**
     * Obtiene las credenciales (suspend)
     */
    @Query("SELECT * FROM listenbrainz_credentials WHERE id = 1")
    suspend fun getCredentials(): ListenBrainzCredentialsEntity?
    
    /**
     * Guarda o actualiza las credenciales
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCredentials(credentials: ListenBrainzCredentialsEntity)
    
    /**
     * Limpia las credenciales (logout)
     */
    @Query("DELETE FROM listenbrainz_credentials")
    suspend fun clearCredentials()
    
    /**
     * Actualiza el timestamp del último sync
     */
    @Query("UPDATE listenbrainz_credentials SET lastSyncTimestamp = :timestamp WHERE id = 1")
    suspend fun updateLastSyncTimestamp(timestamp: Long)
}
