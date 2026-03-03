# 🎯 Haptic Feedback Implementation Plan

**Version:** v1.5.0
**Time Estimate:** 2-3 días
**Impact:** 🔥🔥🔥 (High visibility, quick win)

---

## 📋 Implementation Steps

### Step 1: Create HapticFeedback Utility Class
**File:** `app/src/main/java/com/mardous/booming/util/HapticFeedback.kt`

```kotlin
package com.mardous.booming.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.IntDef

/**
 * Haptic feedback utility for consistent vibration patterns
 */
object HapticFeedback {
    
    @IntDef(HapticType.NONE, HapticType.LIGHT, HapticType.MEDIUM, HapticType.HEAVY)
    @Retention(AnnotationRetention.SOURCE)
    annotation class HapticType {
        companion object {
            const val NONE = 0
            const val LIGHT = 1
            const val MEDIUM = 2
            const val HEAVY = 3
        }
    }
    
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
     * Perform haptic feedback
     */
    fun perform(context: Context, @HapticType.HapticType type: Int = HapticType.MEDIUM) {
        if (!isHapticEnabled(context)) return
        
        val vibrator = getSystemServiceVibrator(context) ?: return
        
        when (type) {
            HapticType.NONE -> return
            HapticType.LIGHT -> performLight(vibrator)
            HapticType.MEDIUM -> performMedium(vibrator)
            HapticType.HEAVY -> performHeavy(vibrator)
        }
    }
    
    /**
     * Perform haptic feedback with custom pattern
     */
    fun performPattern(context: Context, pattern: LongArray, repeat: Int = -1) {
        if (!isHapticEnabled(context)) return
        
        val vibrator = getSystemServiceVibrator(context) ?: return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, repeat))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, repeat)
        }
    }
    
    /**
     * Perform button click haptic
     */
    fun performClick(context: Context) {
        perform(context, HapticType.LIGHT)
    }
    
    /**
     * Perform playback control haptic
     */
    fun performPlayPause(context: Context) {
        performPattern(context, Pattern.PLAY_PAUSE)
    }
    
    /**
     * Perform like/favorite haptic
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
     * Perform error haptic
     */
    fun performError(context: Context) {
        performPattern(context, Pattern.ERROR)
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
        val vibrator = getSystemServiceVibrator(context)
        return vibrator?.hasVibrator() == true
    }
    
    // Private helpers
    
    private fun performLight(vibrator: Vibrator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(BUTTON_CLICK, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(BUTTON_CLICK)
        }
    }
    
    private fun performMedium(vibrator: Vibrator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(BUTTON_PRESS, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(BUTTON_PRESS)
        }
    }
    
    private fun performHeavy(vibrator: Vibrator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPrecomposed(VibrationEffect.EFFECT_HEAVY_CLICK))
        } else {
            performMedium(vibrator)
        }
    }
    
    private fun getSystemServiceVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
}
```

---

### Step 2: Add Haptic Preference Setting
**File:** `app/src/main/res/xml/preferences_playback.xml` (o preferences_ui.xml)

```xml
<PreferenceCategory
    android:title="UI/UX">
    
    <SwitchPreferenceCompat
        android:key="haptic_feedback"
        android:title="Haptic Feedback"
        android:summary="Vibrate on button presses and playback controls"
        android:defaultValue="true"
        android:icon="@drawable/ic_vibration_24dp"/>
    
</PreferenceCategory>
```

---

### Step 3: Integrate with Player UI
**Files to modify:**
- `app/src/main/java/com/mardous/booming/ui/screen/player/AbsPlayerFragment.kt`
- `app/src/main/java/com/mardous/booming/ui/component/MiniPlayer.kt`
- `app/src/main/java/com/mardous/booming/ui/screen/library/SongListAdapter.kt`

**Example integration in AbsPlayerFragment:**

```kotlin
// Play/Pause button
playPauseButton.setOnClickListener {
    HapticFeedback.performPlayPause(requireContext())
    togglePlayPause()
}

// Like button
likeButton.setOnClickListener {
    HapticFeedback.performLike(requireContext())
    toggleLike()
}

// Skip buttons
skipNextButton.setOnClickListener {
    HapticFeedback.performSkip(requireContext())
    playNext()
}

skipPreviousButton.setOnClickListener {
    HapticFeedback.performSkip(requireContext())
    playPrevious()
}

// Shuffle button
shuffleButton.setOnClickListener {
    HapticFeedback.performShuffle(requireContext())
    toggleShuffle()
}
```

---

### Step 4: Add Vibration Permission
Already exists in AndroidManifest.xml:
```xml
<uses-permission android:name="android.permission.VIBRATE"/>
```

---

### Step 5: Test on Device
- [ ] Test on physical device (emulator doesn't have vibration)
- [ ] Test with haptic feedback enabled/disabled
- [ ] Test different patterns
- [ ] Verify no StrictMode violations

---

## 📊 Expected Results

### Before:
- Silent button presses
- No tactile feedback
- User unsure if button was pressed

### After:
- Satisfying tactile feedback on every interaction
- Clear confirmation of button presses
- Premium feel
- Better accessibility for visually impaired users

---

## 🎨 Haptic Patterns

| Action | Pattern | Duration | Feel |
|--------|---------|----------|------|
| Button Click | 10ms | Very short | Subtle tick |
| Play/Pause | 20-10-20ms | Medium | Double tap |
| Like | 50-50-50ms | Long | Heart beat |
| Skip | 30-10-30ms | Medium | Double tap stronger |
| Shuffle | 15-30-15-60ms | Long | Rhythmic pattern |
| Error | 100-50-100ms | Long | Strong warning |

---

## ⚠️ Important Notes

1. **Don't overuse:** Only on important interactions
2. **Respect user preference:** Add setting to disable
3. **Battery impact:** Minimal (vibrator uses ~0.1mAh per activation)
4. **API levels:** Fallback for older Android versions
5. **Testing:** Must test on physical device

---

## 📝 Files to Create/Modify

### Create:
- `app/src/main/java/com/mardous/booming/util/HapticFeedback.kt`
- `app/src/main/res/drawable/ic_vibration_24dp.xml` (icon)

### Modify:
- `app/src/main/res/xml/preferences_playback.xml` (add setting)
- `app/src/main/java/com/mardous/booming/ui/screen/player/AbsPlayerFragment.kt`
- `app/src/main/java/com/mardous/booming/ui/component/MiniPlayer.kt`
- `app/src/main/java/com/mardous/booming/ui/screen/library/SongListAdapter.kt`

---

## ✅ Acceptance Criteria

- [ ] HapticFeedback utility class created
- [ ] All player controls have haptic feedback
- [ ] Setting to enable/disable haptic feedback
- [ ] Works on Android 8.0+ (API 26+)
- [ ] No StrictMode violations
- [ ] Battery impact < 1% per day
- [ ] User feedback positive

---

*Implementation guide created: March 3, 2026*
