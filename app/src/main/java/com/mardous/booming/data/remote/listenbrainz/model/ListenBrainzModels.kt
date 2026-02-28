package com.mardous.booming.data.remote.listenbrainz.model

/**
 * Credenciales de ListenBrainz
 * 
 * @param userToken Token de usuario (se obtiene de listenbrainz.org/settings)
 * @param username Nombre de usuario (opcional, se obtiene tras validar)
 * @param isLoggedIn Si el usuario está conectado
 */
data class ListenBrainzCredentials(
    val userToken: String,
    val username: String? = null,
    val isLoggedIn: Boolean = !userToken.isBlank()
) {
    companion object {
        fun create(userToken: String): ListenBrainzCredentials {
            return ListenBrainzCredentials(
                userToken = userToken.trim(),
                isLoggedIn = userToken.isNotBlank()
            )
        }
        
        fun empty(): ListenBrainzCredentials {
            return ListenBrainzCredentials(
                userToken = "",
                isLoggedIn = false
            )
        }
    }
}

/**
 * Request para scrobble
 */
data class ListenBrainzScrobble(
    val artistName: String,
    val trackName: String,
    val listenedAt: Long? = null,         // Timestamp (null = now playing)
    val releaseName: String? = null,       // Álbum
    val durationMs: Int? = null,           // Duración en ms
    val trackNumber: Int? = null,          // Número de track
    val mbidArtist: String? = null,        // MusicBrainz ID artista
    val mbidRelease: String? = null,       // MusicBrainz ID álbum
    val mbidRecording: String? = null      // MusicBrainz ID track
) {
    /**
     * Verifica si el track cumple para scrobble
     * - Mínimo 30 segundos o 50% del track
     */
    fun isValidForScrobble(playbackDurationMs: Long): Boolean {
        val duration = this.durationMs ?: return false
        val minDuration = minOf(30000, duration / 2)
        return playbackDurationMs >= minDuration
    }
}

/**
 * Respuesta de scrobble
 */
data class ListenBrainzScrobbleResponse(
    val status: String,
    val error: String? = null
)

/**
 * Respuesta de validación de usuario
 */
data class ListenBrainzUserResponse(
    val status: String,
    val userName: String? = null,
    val error: String? = null
)
