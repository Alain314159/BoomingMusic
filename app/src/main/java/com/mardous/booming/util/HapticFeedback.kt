/*
 * Copyright (c) 2026 Christians Martínez Alvarado
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

package com.mardous.booming.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Haptic feedback utility for consistent vibration patterns
 *
 * Features:
 * - Predefined patterns for common actions (click, play/pause, like, skip, etc.)
 * - Respects user preference (can be disabled in settings)
 * - API level aware (uses modern APIs when available)
 * - No StrictMode violations
 *
 * Usage:
 *   HapticFeedback.performClick(context)
 *   HapticFeedback.performPlayPause(context)
 *   HapticFeedback.performLike(context)
 */
object HapticFeedback {

    /**
     * Haptic feedback strength types
     */
    enum class HapticType {
        NONE,
        LIGHT,
        MEDIUM,
        HEAVY
    }

    /**
     * Predefined vibration patterns
     */
    object Pattern {
        // Button click patterns
        const val BUTTON_CLICK = 10L
        const val BUTTON_PRESS = 20L

        // Playback control patterns
        val PLAY_PAUSE = longArrayOf(0, 20, 10, 20)
        val SKIP = longArrayOf(0, 30, 10, 30)
        val SHUFFLE = longArrayOf(0, 15, 30, 15, 60)

        // Like/Favorite pattern (heart beat)
        val LIKE = longArrayOf(0, 50, 50, 50)

        // Navigation patterns
        val NAVIGATION = longArrayOf(0, 20)

        // Error/Warning patterns
        val ERROR = longArrayOf(0, 100, 50, 100)
        val WARNING = longArrayOf(0, 50, 50, 50)
    }

    /**
     * Perform haptic feedback with specified strength
     *
     * @param context Android context
     * @param type Strength of haptic feedback (LIGHT, MEDIUM, HEAVY)
     */
    fun perform(context: Context, type: HapticType = HapticType.MEDIUM) {
        if (!isHapticEnabled(context)) return
        if (!hasVibrator(context)) return

        val vibrator = getVibrator(context) ?: return

        when (type) {
            HapticType.NONE -> return
            HapticType.LIGHT -> performLight(vibrator)
            HapticType.MEDIUM -> performMedium(vibrator)
            HapticType.HEAVY -> performHeavy(vibrator)
        }
    }

    /**
     * Perform haptic feedback with custom pattern
     *
     * @param context Android context
     * @param pattern Vibration pattern (timing array)
     * @param repeat Repeat count (-1 for no repeat)
     */
    fun performPattern(context: Context, pattern: LongArray, repeat: Int = -1) {
        if (!isHapticEnabled(context)) return
        if (!hasVibrator(context)) return

        val vibrator = getVibrator(context) ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, repeat))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, repeat)
        }
    }

    /**
     * Perform button click haptic (light feedback)
     */
    fun performClick(context: Context) {
        perform(context, HapticType.LIGHT)
    }

    /**
     * Perform playback control haptic (play/pause button)
     */
    fun performPlayPause(context: Context) {
        performPattern(context, Pattern.PLAY_PAUSE)
    }

    /**
     * Perform like/favorite haptic (heart beat pattern)
     */
    fun performLike(context: Context) {
        performPattern(context, Pattern.LIKE)
    }

    /**
     * Perform skip track haptic
     */
    fun performSkip(context: Context) {
        performPattern(context, Pattern.SKIP)
    }

    /**
     * Perform shuffle toggle haptic
     */
    fun performShuffle(context: Context) {
        performPattern(context, Pattern.SHUFFLE)
    }

    /**
     * Perform repeat toggle haptic
     */
    fun performRepeat(context: Context) {
        performPattern(context, Pattern.SHUFFLE)
    }

    /**
     * Perform error haptic (strong warning)
     */
    fun performError(context: Context) {
        performPattern(context, Pattern.ERROR)
    }

    /**
     * Perform warning haptic
     */
    fun performWarning(context: Context) {
        performPattern(context, Pattern.WARNING)
    }

    /**
     * Perform navigation haptic (screen transitions)
     */
    fun performNavigation(context: Context) {
        performPattern(context, Pattern.NAVIGATION)
    }

    /**
     * Check if haptic feedback is enabled in settings
     */
    fun isHapticEnabled(context: Context): Boolean {
        val prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean("haptic_feedback", true)
    }

    /**
     * Check if device has vibrator
     */
    fun hasVibrator(context: Context): Boolean {
        val vibrator = getVibrator(context)
        return vibrator?.hasVibrator() == true
    }

    // Private helpers

    private fun performLight(vibrator: Vibrator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(Pattern.BUTTON_CLICK, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(Pattern.BUTTON_CLICK)
        }
    }

    private fun performMedium(vibrator: Vibrator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(Pattern.BUTTON_PRESS, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(Pattern.BUTTON_PRESS)
        }
    }

    private fun performHeavy(vibrator: Vibrator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.EFFECT_HEAVY_CLICK))
        } else {
            performMedium(vibrator)
        }
    }

    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
}
