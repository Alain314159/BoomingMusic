package com.mardous.booming.data.remote.listenbrainz.api

import com.mardous.booming.data.remote.listenbrainz.model.ListenBrainzScrobble
import com.mardous.booming.data.remote.listenbrainz.model.ListenBrainzScrobbleResponse
import com.mardous.booming.data.remote.listenbrainz.model.ListenBrainzUserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API Client para ListenBrainz
 * 
 * Documentación: https://listenbrainz.readthedocs.io/
 * 
 * NO requiere API Key - los usuarios usan su token personal
 */
@Singleton
class ListenBrainzApi @Inject constructor(
    private val httpClient: HttpClient
) {
    
    companion object {
        private const val BASE_URL = "https://api.listenbrainz.org/1"
        
        // Endpoints
        private const val ENDPOINT_SUBMIT_LISTENS = "/submit-listens"
        private const val ENDPOINT_VALIDATE_TOKEN = "/validate-token"
        private const val ENDPOINT_USER = "/user"
    }
    
    /**
     * Valida el token del usuario y obtiene el username
     */
    suspend fun validateToken(userToken: String): Result<ListenBrainzUserResponse> {
        return runCatching {
            val response = httpClient.get("$BASE_URL$ENDPOINT_VALIDATE_TOKEN") {
                header("Authorization", "Token $userToken")
            }
            
            response.body<ListenBrainzUserResponse>()
        }
    }
    
    /**
     * Envía un scrobble individual
     */
    suspend fun scrobble(
        userToken: String,
        scrobble: ListenBrainzScrobble
    ): Result<ListenBrainzScrobbleResponse> {
        return submitListens(userToken, listOf(scrobble), "single")
    }
    
    /**
     * Envía múltiples scrobbles (batch)
     */
    suspend fun submitListens(
        userToken: String,
        scrobbles: List<ListenBrainzScrobble>,
        listenType: String = "single" // "single" o "playing_now"
    ): Result<ListenBrainzScrobbleResponse> {
        return runCatching {
            val payload = buildSubmitPayload(scrobbles, listenType)
            
            val response = httpClient.post("$BASE_URL$ENDPOINT_SUBMIT_LISTENS") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Token $userToken")
                setBody(payload)
            }
            
            response.body<ListenBrainzScrobbleResponse>()
        }
    }
    
    /**
     * Actualiza "Now Playing" (escuchando actualmente)
     */
    suspend fun updateNowPlaying(
        userToken: String,
        scrobble: ListenBrainzScrobble
    ): Result<ListenBrainzScrobbleResponse> {
        return submitListens(userToken, listOf(scrobble), "playing_now")
    }
    
    /**
     * Construye el payload para submit
     */
    private fun buildSubmitPayload(
        scrobbles: List<ListenBrainzScrobble>,
        listenType: String
    ): SubmitPayload {
        val listens = scrobbles.map { scrobble ->
            ListenPayload(
                trackMetadata = TrackMetadata(
                    artistName = scrobble.artistName,
                    trackName = scrobble.trackName,
                    releaseName = scrobble.releaseName,
                    durationMs = scrobble.durationMs,
                    trackNumber = scrobble.trackNumber,
                    mbid = MbidMetadata(
                        artistMbid = scrobble.mbidArtist,
                        releaseMbid = scrobble.mbidRelease,
                        recordingMbid = scrobble.mbidRecording
                    )
                ),
                listenedAt = scrobble.listenedAt
            )
        }
        
        return SubmitPayload(
            listenType = listenType,
            listens = listens
        )
    }
}

// ==================== PAYLOAD MODELS ====================

@Serializable
data class SubmitPayload(
    @SerialName("listen_type")
    val listenType: String,
    
    @SerialName("payload")
    val listens: List<ListenPayload>
)

@Serializable
data class ListenPayload(
    @SerialName("track_metadata")
    val trackMetadata: TrackMetadata,
    
    @SerialName("listened_at")
    val listenedAt: Long? = null
)

@Serializable
data class TrackMetadata(
    @SerialName("artist_name")
    val artistName: String,
    
    @SerialName("track_name")
    val trackName: String,
    
    @SerialName("release_name")
    val releaseName: String? = null,
    
    @SerialName("duration_ms")
    val durationMs: Int? = null,
    
    @SerialName("tracknumber")
    val trackNumber: Int? = null,
    
    @SerialName("additional_info")
    val mbid: MbidMetadata? = null
)

@Serializable
data class MbidMetadata(
    @SerialName("artist_mbid")
    val artistMbid: String? = null,
    
    @SerialName("release_mbid")
    val releaseMbid: String? = null,
    
    @SerialName("recording_mbid")
    val recordingMbid: String? = null
)

// ==================== RESPONSE MODELS ====================

@Serializable
data class ListenBrainzUserResponse(
    @SerialName("status")
    val status: String,
    
    @SerialName("user_name")
    val userName: String? = null,
    
    @SerialName("error")
    val error: String? = null
)

@Serializable
data class ListenBrainzScrobbleResponse(
    @SerialName("status")
    val status: String,
    
    @SerialName("error")
    val error: String? = null,
    
    @SerialName("message")
    val message: String? = null
)
