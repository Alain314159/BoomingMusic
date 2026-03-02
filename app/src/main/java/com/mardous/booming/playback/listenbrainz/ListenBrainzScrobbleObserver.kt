package com.mardous.booming.playback.listenbrainz

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.mardous.booming.data.remote.listenbrainz.model.ListenBrainzScrobble
import com.mardous.booming.data.remote.listenbrainz.service.ListenBrainzScrobbleService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Observer que monitorea la reproducción y envía scrobbles a ListenBrainz
 * 
 * Se registra como listener del player y detecta:
 * - Inicio de reproducción
 * - Fin de reproducción
 * - Duración escuchada
 * 
 * Reglas de scrobbling:
 * - Track > 30 segundos escuchados
 * - O 50% del track si es menor a 30 segundos
 */
@Singleton
class ListenBrainzScrobbleObserver @Inject constructor(
    private val scrobbleService: ListenBrainzScrobbleService
) : Player.Listener {
    
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var currentScrobbleJob: Job? = null
    
    // Estado actual de reproducción
    private var currentMediaItem: MediaItem? = null
    private var playbackStartTimeMs: Long = 0
    private var totalPlayedDurationMs: Long = 0
    private var isPaused: Boolean = false
    private var pauseStartTimeMs: Long = 0
    
    /**
     * Se llama cuando cambia el track actual
     */
    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        
        // Si había un track anterior, finalizar su scrobble
        currentScrobbleJob?.cancel()
        
        // Guardar nuevo track
        currentMediaItem = mediaItem
        
        if (mediaItem != null) {
            // Iniciar nuevo track
            playbackStartTimeMs = System.currentTimeMillis()
            totalPlayedDurationMs = 0
            isPaused = false
            
            // Enviar "Now Playing"
            sendNowPlaying(mediaItem)
        } else {
            // Cola terminada
            currentMediaItem = null
        }
    }
    
    /**
     * Se llama cuando cambia el estado de reproducción
     */
    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)
        
        when (state) {
            Player.STATE_READY -> {
                // Playback iniciado/resumido
                if (isPaused) {
                    // Reanudar desde pausa
                    totalPlayedDurationMs += (System.currentTimeMillis() - pauseStartTimeMs)
                    isPaused = false
                    startScrobbleTimer()
                }
            }
            
            Player.STATE_ENDED -> {
                // Track terminado
                finishTrack()
            }
            
            Player.STATE_IDLE -> {
                // Player detenido
                currentScrobbleJob?.cancel()
            }
        }
    }
    
    /**
     * Se llama cuando el player hace pause
     */
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        
        if (!isPlaying && currentMediaItem != null) {
            // Pausado
            isPaused = true
            pauseStartTimeMs = System.currentTimeMillis()
            currentScrobbleJob?.cancel()
        }
    }
    
    /**
     * Envía estado "Now Playing" a ListenBrainz
     */
    private fun sendNowPlaying(mediaItem: MediaItem) {
        coroutineScope.launch {
            val scrobble = mediaItem.toListenBrainzScrobble()
            scrobbleService.updateNowPlaying(scrobble)
        }
    }
    
    /**
     * Inicia timer para scrobble
     * Verifica cada 5 segundos si ya cumple los requisitos
     */
    private fun startScrobbleTimer() {
        currentScrobbleJob?.cancel()
        
        currentScrobbleJob = coroutineScope.launch {
            while (true) {
                delay(5000) // Verificar cada 5 segundos
                
                val currentMediaItem = currentMediaItem ?: break
                val playedDuration = getPlayedDuration()
                
                // Verificar si cumple para scrobble
                if (shouldScrobble(currentMediaItem, playedDuration)) {
                    submitScrobble(currentMediaItem, playedDuration)
                    break // Ya se scrobbleó, salir del loop
                }
            }
        }
    }
    
    /**
     * Finaliza el track actual y envía scrobble si corresponde
     */
    private fun finishTrack() {
        val mediaItem = currentMediaItem ?: return
        val playedDuration = getPlayedDuration()
        
        if (shouldScrobble(mediaItem, playedDuration)) {
            submitScrobble(mediaItem, playedDuration)
        }
        
        // Resetear estado
        currentScrobbleJob?.cancel()
        currentMediaItem = null
        totalPlayedDurationMs = 0
    }
    
    /**
     * Calcula duración total escuchada
     */
    private fun getPlayedDuration(): Long {
        val baseDuration = totalPlayedDurationMs
        return if (isPaused) {
            baseDuration
        } else {
            baseDuration + (System.currentTimeMillis() - playbackStartTimeMs)
        }
    }
    
    /**
     * Verifica si el track cumple los requisitos para scrobble
     */
    private fun shouldScrobble(mediaItem: MediaItem, playedDurationMs: Long): Boolean {
        val trackDurationMs = mediaItem.mediaMetadata.extras?.getLong("duration") ?: 0L
        
        if (trackDurationMs <= 0) return false
        
        // Regla: 30 segundos o 50% del track (lo que sea menor)
        val minDuration = minOf(30000, trackDurationMs / 2)
        
        return playedDurationMs >= minDuration
    }
    
    /**
     * Envía scrobble a ListenBrainz
     */
    private fun submitScrobble(mediaItem: MediaItem, playedDurationMs: Long) {
        coroutineScope.launch {
            try {
                val scrobble = mediaItem.toListenBrainzScrobble(
                    listenedAt = playbackStartTimeMs / 1000 // Convertir a segundos
                )

                scrobbleService.submitScrobble(scrobble)
            } catch (e: Exception) {
                Log.e(TAG, "Error submitting scrobble", e)
            }
        }
    }

    companion object {
        private const val TAG = "ListenBrainzObserver"
    }
}

/**
 * Extensión para convertir MediaItem a ListenBrainzScrobble
 */
private fun MediaItem.toListenBrainzScrobble(
    listenedAt: Long? = null
): ListenBrainzScrobble {
    val metadata = this.mediaMetadata
    
    return ListenBrainzScrobble(
        artistName = metadata.artist?.toString() ?: "Unknown Artist",
        trackName = metadata.title?.toString() ?: "Unknown Track",
        releaseName = metadata.albumTitle?.toString(),
        durationMs = metadata.extras?.getLong("duration")?.toInt(),
        trackNumber = metadata.trackNumber,
        listenedAt = listenedAt,
        mbidArtist = metadata.extras?.getString("artist_mbid"),
        mbidRelease = metadata.extras?.getString("album_mbid"),
        mbidRecording = metadata.extras?.getString("track_mbid")
    )
}
