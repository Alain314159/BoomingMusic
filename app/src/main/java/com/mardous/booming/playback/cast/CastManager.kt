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

package com.mardous.booming.playback.cast

import android.content.Context
import androidx.media3.cast.CastPlayer
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastState
import com.google.android.gms.cast.framework.CastStateListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages Cast functionality and CastPlayer lifecycle.
 * Handles switching between local (ExoPlayer) and remote (CastPlayer) playback.
 */
class CastManager private constructor(
    private val context: Context
) : CastStateListener {

    interface Listener {
        fun onCastStateChanged(isCastSession: Boolean)
        fun onCastAvailabilityChanged(isCastAvailable: Boolean)
    }

    private val listeners = mutableListOf<Listener>()

    /**
     * The current active player (either ExoPlayer or CastPlayer).
     */
    var currentPlayer: Player? = null
        private set

    /**
     * The local ExoPlayer instance.
     */
    private var exoPlayer: ExoPlayer? = null

    /**
     * The CastPlayer instance for remote playback.
     */
    private var castPlayer: CastPlayer? = null

    /**
     * MediaSession for managing media sessions.
     */
    private var mediaSession: MediaSession? = null

    /**
     * StateFlow indicating if a Cast session is active.
     */
    private val _isCastSession = MutableStateFlow(false)
    val isCastSession: StateFlow<Boolean> = _isCastSession.asStateFlow()

    /**
     * StateFlow indicating if Cast is available.
     */
    private val _isCastAvailable = MutableStateFlow(false)
    val isCastAvailable: StateFlow<Boolean> = _isCastAvailable.asStateFlow()

    init {
        try {
            CastContext.getSharedInstance(context).addCastStateListener(this)
            updateCastState(CastContext.getSharedInstance(context).castState)
        } catch (e: Exception) {
            // Cast not available or initialization failed
            _isCastAvailable.value = false
        }
    }

    /**
     * Initializes the CastManager with an ExoPlayer instance.
     */
    fun initialize(exoPlayer: ExoPlayer) {
        this.exoPlayer = exoPlayer
        this.currentPlayer = exoPlayer

        // Create CastPlayer
        castPlayer = CastPlayer.Builder(context)
            .setLocalPlayer(exoPlayer)
            .build()

        // Start with local player
        currentPlayer = exoPlayer
    }

    /**
     * Sets the MediaSession for the CastManager.
     */
    fun setMediaSession(session: MediaSession) {
        this.mediaSession = session
        updatePlayerForSession()
    }

    /**
     * Updates the player used in the MediaSession based on cast state.
     */
    private fun updatePlayerForSession() {
        mediaSession?.let { session ->
            session.setPlayer(currentPlayer)
        }
    }

    /**
     * Switches to Cast playback if a Cast session is active.
     */
    fun switchToCastIfAvailable() {
        if (_isCastSession.value && castPlayer != null) {
            switchToPlayer(castPlayer!!)
        }
    }

    /**
     * Switches to the specified player.
     */
    private fun switchToPlayer(newPlayer: Player) {
        if (currentPlayer == newPlayer) return

        val oldPlayer = currentPlayer
        currentPlayer = newPlayer

        // Transfer playback state
        if (oldPlayer != null && newPlayer != oldPlayer) {
            // Copy current playback state to new player
            newPlayer.apply {
                setMediaItems(
                    oldPlayer.currentMediaItems,
                    oldPlayer.currentMediaItemIndex,
                    oldPlayer.currentPosition
                )
                playWhenReady = oldPlayer.playWhenReady
                prepare()
            }
        }

        updatePlayerForSession()
        notifyCastStateChanged()
    }

    /**
     * Releases all resources.
     */
    fun release() {
        try {
            CastContext.getSharedInstance(context).removeCastStateListener(this)
        } catch (e: Exception) {
            // Ignore
        }

        castPlayer?.release()
        exoPlayer = null
        castPlayer = null
        currentPlayer = null
        mediaSession = null
    }

    /**
     * Adds a listener for cast state changes.
     */
    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    /**
     * Removes a listener.
     */
    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    private fun notifyCastStateChanged() {
        listeners.forEach { it.onCastStateChanged(_isCastSession.value) }
    }

    private fun notifyCastAvailabilityChanged() {
        listeners.forEach { it.onCastAvailabilityChanged(_isCastAvailable.value) }
    }

    override fun onCastStateChanged(state: Int) {
        updateCastState(state)
    }

    private fun updateCastState(state: Int) {
        val wasAvailable = _isCastAvailable.value
        val wasCastSession = _isCastSession.value

        _isCastAvailable.value = state != CastState.NO_DEVICES_AVAILABLE
        _isCastSession.value = state == CastState.CONNECTED

        if (_isCastAvailable.value != wasAvailable) {
            notifyCastAvailabilityChanged()
        }

        if (_isCastSession.value != wasCastSession) {
            if (_isCastSession.value) {
                // Cast session connected - switch to CastPlayer
                castPlayer?.let { switchToPlayer(it) }
            } else {
                // Cast session disconnected - switch back to ExoPlayer
                exoPlayer?.let { switchToPlayer(it) }
            }
            notifyCastStateChanged()
        }
    }

    companion object {
        @Volatile
        private var instance: CastManager? = null

        fun getInstance(context: Context): CastManager {
            return instance ?: synchronized(this) {
                instance ?: CastManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}
