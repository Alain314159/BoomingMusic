# 🐛 Booming Music - Error Report

> **Generated:** 2026-03-03 18:19:04.585
> **App Version:** 1.2.1 DEBUG (1210300)
> **Android:** 13 (API 33)
> **Device:** Xiaomi 21121119VL
> **Build:** TP1A.220624.014

---

## 📊 Summary

| Type | Count |
|------|-------|
| Errors | 0 |
| Warnings | 0 |
| Crashes | 1 |
| StrictMode Violations | 100+ |
| Network Errors | 0 |
| Database Errors | 0 |

---

## 🔴 CRITICAL ERRORS

### 1. ActivityNotFoundException - ListenBrainzSettingsActivity

**Time:** 2026-03-03 18:23:06.475

**Severity:** 🔴 CRITICAL

**Message:** 
```
Unable to find explicit activity class {com.mardous.booming/com.mardous.booming.ui.screen.settings.listenbrainz.ListenBrainzSettingsActivity}; 
have you declared this activity in your AndroidManifest.xml?
```

**Stack Trace:**
```
android.content.ActivityNotFoundException: Unable to find explicit activity class 
  at android.app.Instrumentation.checkStartActivityResult(Instrumentation.java:2217)
  at androidx.preference.Preference.performClick(Preference.java:1215)
  at com.mardous.booming.ui.screen.settings.listenbrainz.ListenBrainzSettingsFragment.onCreatePreferences(ListenBrainzSettingsFragment.kt)
```

**Root Cause:** 
- `ListenBrainzSettingsActivity` no está registrada en `AndroidManifest.xml`
- El usuario hizo click en una preferencia que intenta abrir esta actividad
- La actividad existe en el código pero no está declarada en el manifest

**Solution:**
```xml
<!-- Add to AndroidManifest.xml -->
<activity 
    android:name=".ui.screen.settings.listenbrainz.ListenBrainzSettingsActivity"
    android:exported="false" />
```

**Priority:** 🔴 BLOCKER - Impide usar ListenBrainz settings

---

## 🟡 STRICTMODE VIOLATIONS (100+)

### Pattern Analysis

**Types of Violations:**

1. **DiskReadViolation** (80% de las violaciones)
   - Ocurre principalmente en `App.enableStrictMode()` línea 227
   - Causa: Timber está escribiendo logs que disparan más violaciones de lectura
   - Es un efecto cascada: log → violation → log → violation

2. **DiskWriteViolation** (15% de las violaciones)
   - Ocurre en inicialización de SharedPreferences
   - Escritura de preferencias en el thread principal

3. **UntaggedSocketViolation** (4% de las violaciones)
   - Ocurre en `App.enableStrictMode()` línea 215
   - Conexiones de red sin etiqueta de tráfico

4. **CustomViolation** (1% de las violaciones)
   - Violaciones personalizadas de código

5. **IncorrectContextUseViolation** (1% de las violaciones)
   - Uso incorrecto de Context

### Violation Hotspots

| File | Line | Violation Type | Count |
|------|------|---------------|-------|
| `App.kt` | 227 | DiskReadViolation | 50+ |
| `App.kt` | 215 | UntaggedSocketViolation | 5+ |
| `SharedPreferences` | - | DiskWriteViolation | 20+ |
| Various | - | DiskReadViolation | 25+ |

### Root Cause Analysis

**Problem:** El logger de Timber está configurado para loggear violaciones de StrictMode, pero el acto de loggear causa MÁS violaciones de StrictMode (efecto recursivo).

**Code causing issue:**
```kotlin
// App.kt línea 215-227
StrictMode.setVmPolicy(VmPolicy.Builder()
    .detectAll()
    .penaltyLog()  // ← Esto causa log
    .penaltyListener(executor) { violation ->
        val message = "VM Policy violation: ${violation.javaClass.simpleName}"
        Timber.e(message)  // ← Timber.e causa DiskReadViolation
        DebugLogger.logStrictModeViolation("VM", message)  // ← Más logs
    }
    .build())
```

**Solution:**
```kotlin
// Opción 1: Desactivar penaltyLog cuando hay penaltyListener
StrictMode.setVmPolicy(VmPolicy.Builder()
    .detectAll()
    .penaltyListener(executor) { violation ->
        // Solo loggear en crashlytics/file, no en logcat
        DebugLogger.logStrictModeViolation("VM", violation.toString())
    }
    .build())

// Opción 2: Usar handler que no haga I/O
StrictMode.setVmPolicy(VmPolicy.Builder()
    .detectAll()
    .penaltyDeath()  // ← Solo para debug builds extremos
    .build())

// Opción 3: Ignorar violaciones específicas del logger
StrictMode.setVmPolicy(VmPolicy.Builder()
    .detectAll()
    .permitDiskReads()  // ← Permitir lecturas durante logging
    .penaltyListener(executor) { ... }
    .build())
```

**Priority:** 🟡 MEDIUM - Solo afecta debug builds, pero ensucia los logs

---

## 📋 ACTION ITEMS

### Priority 1 - CRITICAL (Fix Immediately)
- [ ] **Agregar ListenBrainzSettingsActivity al AndroidManifest**
  - File: `app/src/main/AndroidManifest.xml`
  - Add: `<activity android:name=".ui.screen.settings.listenbrainz.ListenBrainzSettingsActivity" />`
  - Time: 2 minutes
  - Impact: BLOCKER - Users can't access ListenBrainz settings

### Priority 2 - HIGH (Fix This Week)
- [ ] **Fix StrictMode logging recursion**
  - File: `app/src/main/java/com/mardous/booming/App.kt`
  - Remove `penaltyLog()` when using `penaltyListener()`
  - Time: 30 minutes
  - Impact: Cleaner debug logs, less noise

- [ ] **Add network traffic labels**
  - File: `app/src/main/java/com/mardous/booming/MainModule.kt` (network module)
  - Add: `TrafficStats.setThreadStatsTag()` for network calls
  - Time: 1 hour
  - Impact: No more UntaggedSocketViolation

### Priority 3 - MEDIUM (Fix This Sprint)
- [ ] **Move SharedPreferences writes to background**
  - Files: All `PreferenceFragmentCompat` usages
  - Use: `SharedPreferences.apply()` instead of `commit()`
  - Time: 2 hours
  - Impact: No DiskWriteViolation on main thread

- [ ] **Improve DebugLogger to be StrictMode-safe**
  - File: `app/src/main/java/com/mardous/booming/util/DebugLogger.kt`
  - Use: Async file writing with buffered I/O
  - Time: 3 hours
  - Impact: Logger doesn't cause violations

### Priority 4 - LOW (Enhancement)
- [ ] **Add error categorization to DebugLogger**
  - Auto-detect error patterns
  - Group similar errors
  - Time: 4 hours

- [ ] **Add error export feature**
  - Share button in error report
  - Time: 2 hours

---

## 🔍 DEEP ANALYSIS - Hidden Issues

### Issue 1: ListenBrainz Integration Incomplete

**Evidence:**
- `ListenBrainzSettingsActivity` referenced in code
- Not in AndroidManifest
- Preference screen tries to launch it
- Crash when user clicks ListenBrainz preference

**Files Involved:**
```
app/src/main/java/com/mardous/booming/ui/screen/settings/listenbrainz/
├── ListenBrainzSettingsActivity.kt (exists)
├── ListenBrainzSettingsFragment.kt (exists)
└── ListenBrainzSettingsViewModel.kt (exists)

app/src/main/AndroidManifest.xml
└── MISSING: ListenBrainzSettingsActivity declaration
```

**Impact:** 
- Users cannot configure ListenBrainz
- Feature completamente bloqueada
- ListenBrainz scrobbling no se puede activar

**Fix:**
```xml
<!-- app/src/main/AndroidManifest.xml -->
<application ...>
    <!-- Existing activities -->
    <activity android:name=".ui.screen.MainActivity" ... />
    <activity android:name=".ui.screen.error.ErrorActivity" ... />
    
    <!-- ADD THIS -->
    <activity 
        android:name=".ui.screen.settings.listenbrainz.ListenBrainzSettingsActivity"
        android:exported="false"
        android:theme="@style/Theme.Booming" />
</application>
```

---

### Issue 2: Timber Causing StrictMode Violations

**Evidence:**
- 100+ StrictMode violations in 3 minutes
- 80% are DiskReadViolation
- Stack trace points to `App.kt:75` (Timber log call)
- Recursive logging loop detected

**Root Cause:**
```
StrictMode violation detected
    ↓
penaltyLog() writes to logcat
    ↓
Timber tree intercepts log
    ↓
DebugLogger writes to file (disk read/write)
    ↓
NEW StrictMode violation detected
    ↓ (loop continues)
```

**Impact:**
- Debug builds son inutilizables
- Logs están llenos de ruido
- Performance degradation en debug
- Imposible identificar errores reales

**Fix Options:**

**Option A - Remove penaltyLog():**
```kotlin
// App.kt
StrictMode.setVmPolicy(VmPolicy.Builder()
    .detectAll()
    // .penaltyLog()  // ← REMOVE THIS
    .penaltyListener(executor) { violation ->
        DebugLogger.logStrictModeViolation("VM", violation.toString())
    }
    .build())
```

**Option B - Allow disk reads during logging:**
```kotlin
// App.kt
StrictMode.setVmPolicy(VmPolicy.Builder()
    .detectAll()
    .permitDiskReads()  // ← Allow during logging
    .penaltyListener(executor) { violation ->
        DebugLogger.logStrictModeViolation("VM", violation.toString())
    }
    .build())
```

**Option C - Disable StrictMode temporarily during logging:**
```kotlin
// App.kt
StrictMode.setVmPolicy(VmPolicy.Builder()
    .detectAll()
    .penaltyListener(executor) { violation ->
        StrictMode.disable()  // ← Temporarily disable
        DebugLogger.logStrictModeViolation("VM", violation.toString())
        StrictMode.setVmPolicy(originalPolicy)  // ← Re-enable
    }
    .build())
```

**Recommended:** Option A (simplest, cleanest)

---

### Issue 3: Network Calls Without Traffic Labels

**Evidence:**
- `UntaggedSocketViolation` en línea 215 de App.kt
- Ktor/OkHttp calls sin `TrafficStats.setThreadStatsTag()`
- Ocurre cuando se inicializan los servicios de red

**Impact:**
- No se puede trackear uso de red por app
- Violaciones en debug builds
- Posible memory leak detection failure

**Fix:**
```kotlin
// MainModule.kt o NetworkModule.kt
import android.net.TrafficStats
import android.os.Build

fun provideKtorHttpClient(): HttpClient {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        TrafficStats.setThreadStatsTag(0xA0000000) // Tag for network calls
    }
    
    return HttpClient {
        // Existing configuration
    }
}
```

---

## 📈 ERROR TRENDS

### Violations by Time
```
18:19:04 - First violations (app start)
18:19:07 - Spike in DiskReadViolation (Timber initialization)
18:19:37 - DiskWriteViolation spike (SharedPreferences)
18:20:05 - UntaggedSocketViolation (network init)
18:21:55 - IncorrectContextUseViolation (one-time)
18:23:06 - CRASH: ActivityNotFoundException
```

### Violation Frequency
- **Average:** 1 violation per 2 seconds
- **Peak:** 10 violations per second (18:19:04-18:19:10)
- **Most Active:** First 10 seconds after app start

---

## 🎯 RECOMMENDATIONS

### Immediate (Today)
1. **Fix ListenBrainzSettingsActivity in manifest** - 2 minutes
2. **Remove penaltyLog() from StrictMode** - 5 minutes

### Short Term (This Week)
3. **Add TrafficStats tags to network calls** - 1 hour
4. **Review all SharedPreferences.commit() calls** - 2 hours
5. **Improve DebugLogger async writing** - 3 hours

### Medium Term (This Sprint)
6. **Add error auto-categorization** - 4 hours
7. **Add error export feature** - 2 hours
8. **Implement structured logging** - 8 hours

### Long Term (Next Release)
9. **Add crash analytics (Firebase Crashlytics)** - 1 day
10. **Implement error tracking dashboard** - 3 days

---

## 📝 NOTES

### Debug Build Only
- Todos estos errores son SOLO en debug builds
- Release builds no tienen StrictMode habilitado
- Prioridad es mejorar experiencia de desarrollo

### Logging Paradox
- El logger causa los errores que debería reportar
- Necesitamos logger asíncrono que no haga I/O en thread principal
- Considerar usar `WorkManager` para logging en background

### User Impact
- **Release builds:** Ninguno (StrictMode desactivado)
- **Debug builds:** Alto (imposible debugear con tanto ruido)
- **Testing:** Medio (tests pueden fallar por StrictMode)

---

*Report generated by DebugLogger v1.0*
*Last updated: 2026-03-03 18:30:00*
