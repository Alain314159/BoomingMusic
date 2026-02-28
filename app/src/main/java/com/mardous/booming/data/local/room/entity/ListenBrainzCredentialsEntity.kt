package com.mardous.booming.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad para guardar credenciales de ListenBrainz en Room
 */
@Entity(tableName = "listenbrainz_credentials")
data class ListenBrainzCredentialsEntity(
    @PrimaryKey val id: Int = 1,
    val userToken: String,
    val username: String? = null
) {
    /**
     * Convierte a modelo de dominio
     */
    fun toDomain(): com.mardous.booming.data.remote.listenbrainz.model.ListenBrainzCredentials {
        return com.mardous.booming.data.remote.listenbrainz.model.ListenBrainzCredentials(
            userToken = userToken,
            username = username,
            isLoggedIn = true
        )
    }
    
    companion object {
        /**
         * Crea desde modelo de dominio
         */
        fun fromDomain(credentials: com.mardous.booming.data.remote.listenbrainz.model.ListenBrainzCredentials): ListenBrainzCredentialsEntity {
            return ListenBrainzCredentialsEntity(
                userToken = credentials.userToken,
                username = credentials.username
            )
        }
    }
}
