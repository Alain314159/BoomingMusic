package com.mardous.booming.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mardous.booming.data.remote.listenbrainz.model.ListenBrainzScrobble

/**
 * Entidad para la cola de scrobbles pendientes de ListenBrainz
 * 
 * Se usa cuando el usuario no tiene conexión y se sincroniza después
 */
@Entity(tableName = "listenbrainz_scrobble_queue")
data class ListenBrainzScrobbleQueueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val artistName: String,
    val trackName: String,
    val releaseName: String?,
    val listenedAt: Long?,
    val durationMs: Int?,
    val trackNumber: Int?,
    val mbidArtist: String?,
    val mbidRelease: String?,
    val mbidRecording: String?,
    val retryCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Convierte a ListenBrainzScrobble para enviar
     */
    fun toScrobble(): ListenBrainzScrobble {
        return ListenBrainzScrobble(
            artistName = artistName,
            trackName = trackName,
            releaseName = releaseName,
            listenedAt = listenedAt,
            durationMs = durationMs,
            trackNumber = trackNumber,
            mbidArtist = mbidArtist,
            mbidRelease = mbidRelease,
            mbidRecording = mbidRecording
        )
    }
    
    companion object {
        /**
         * Crea desde un ListenBrainzScrobble
         */
        fun fromScrobble(scrobble: ListenBrainzScrobble): ListenBrainzScrobbleQueueEntity {
            return ListenBrainzScrobbleQueueEntity(
                artistName = scrobble.artistName,
                trackName = scrobble.trackName,
                releaseName = scrobble.releaseName,
                listenedAt = scrobble.listenedAt,
                durationMs = scrobble.durationMs,
                trackNumber = scrobble.trackNumber,
                mbidArtist = scrobble.mbidArtist,
                mbidRelease = scrobble.mbidRelease,
                mbidRecording = scrobble.mbidRecording,
                retryCount = 0
            )
        }
    }
}
