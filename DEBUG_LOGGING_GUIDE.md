# Debug Logging Guide - Booming Music

## Overview

The debug build of Booming Music now includes comprehensive file-based logging that automatically writes all errors, warnings, and debug information to a log file in the Downloads folder.

## Log File Location

```
/Downloads/BoomingMusic/debug.log
```

## Features

### Automatic Logging

The debug logger automatically captures:

- ✅ **All log levels**: Verbose, Debug, Info, Warning, Error, Wtf
- ✅ **Uncaught exceptions**: Crashes are logged before the app terminates
- ✅ **StrictMode violations**: Thread and VM policy violations
- ✅ **Network requests**: URL, method, status code, duration, errors
- ✅ **Database operations**: Queries, performance, row counts
- ✅ **Memory usage**: Allocated, used, and total memory
- ✅ **Lifecycle events**: Activity/Fragment lifecycle changes
- ✅ **Permission results**: Granted/denied permissions
- ✅ **Background tasks**: Worker execution with timing
- ✅ **UI rendering**: Frame drops and render time
- ✅ **Configuration changes**: Settings and preference changes

### Log Format

Each log entry includes:

```
[2026-03-03 10:15:30.123] [ERROR] [Thread:main] [TAG] Message
```

- **Timestamp**: Precise to milliseconds
- **Level**: VERBOSE, DEBUG, INFO, WARNING, ERROR, FATAL
- **Thread**: Name of the thread where the log was created
- **Tag**: Component or category tag
- **Message**: The actual log message
- **Stack trace**: Full stack trace for exceptions

### System Information on Startup

When the app starts, the log file includes:

```
[SYSTEM] Debug logger initialized - File: /storage/emulated/0/Downloads/BoomingMusic/debug.log
[SYSTEM] App version: 1.2.1 DEBUG (1210300)
[SYSTEM] Android version: 13 (API 33)
[SYSTEM] Device: Google Pixel 7 (proton)
[SYSTEM] Build: TQ3A.230901.001
[SYSTEM] CPU ABI: arm64-v8a, armeabi-v7a, armeabi
[SYSTEM] Available processors: 8
[SYSTEM] Max memory: 512 MB
[SYSTEM] Storage - Total: 128 GB | Free: 64 GB
```

## Usage

### Basic Logging

```kotlin
// Verbose
DebugLogger.v("TAG", "Verbose message")

// Debug
DebugLogger.d("TAG", "Debug message")

// Info
DebugLogger.i("TAG", "Info message")

// Warning
DebugLogger.w("TAG", "Warning message")
DebugLogger.w("TAG", "Warning with exception", exception)

// Error
DebugLogger.e("TAG", "Error message")
DebugLogger.e("TAG", "Error with exception", exception)

// What a Terrible Failure
DebugLogger.wtf("TAG", "WTF message")
```

### Advanced Logging

```kotlin
// Log exceptions manually
DebugLogger.logException("TAG", exception, "Context: User clicked button")

// Network requests
DebugLogger.logNetworkRequest(
    url = "https://api.example.com/songs",
    method = "GET",
    startTime = System.currentTimeMillis() - 500,
    endTime = System.currentTimeMillis(),
    statusCode = 200,
    error = null
)

// Database queries
DebugLogger.logDatabaseQuery(
    query = "SELECT * FROM songs WHERE artist_id = ?",
    table = "songs",
    durationMs = 45,
    rowCount = 150,
    success = true
)

// Memory usage
val runtime = Runtime.getRuntime()
DebugLogger.logMemoryUsage(
    allocatedBytes = runtime.totalMemory(),
    freeBytes = runtime.freeMemory(),
    totalBytes = runtime.maxMemory()
)

// Lifecycle events
DebugLogger.logLifecycleEvent("MainActivity", "onCreate")
DebugLogger.logLifecycleEvent("PlayerFragment", "onStart")

// Permission results
DebugLogger.logPermissionResult(
    permission = "android.permission.READ_MEDIA_AUDIO",
    granted = true,
    rationale = false
)

// Background tasks
DebugLogger.logBackgroundTask(
    taskName = "LibraryScan",
    durationMs = 5000,
    success = true,
    error = null
)

// UI rendering
DebugLogger.logUiRendering(
    component = "SongList",
    renderTimeMs = 120,
    frameDrop = true
)

// Configuration changes
DebugLogger.logConfigurationChange(
    changeType = "Theme",
    oldValue = "Light",
    newValue = "Dark"
)

// StrictMode violations
DebugLogger.logStrictModeViolation(
    policyType = "Thread",
    message = "Disk read detected on main thread",
    throwable = violation
)
```

### Using Timber (Recommended for Most Cases)

The app uses Timber for convenient logging:

```kotlin
import timber.log.Timber

// These automatically go to file in debug builds
Timber.v("Verbose message")
Timber.d("Debug message")
Timber.i("Info message")
Timber.w("Warning message")
Timber.e(exception, "Error message")

// Tagged logging
Timber.tag("Network").d("Request to %s", url)
```

## Log File Management

### Automatic Rotation

When the log file exceeds **5 MB**, it's automatically rotated:

- Current log: `debug.log`
- Old log: `debug_old.log` (previous log, kept for reference)

### Manual Log Access

You can access the log file path programmatically:

```kotlin
val logPath = DebugLogger.getLogFilePath()
// Returns: "/storage/emulated/0/Downloads/BoomingMusic/debug.log"

val isInitialized = DebugLogger.isInitialized()
// Returns: true if logger is active
```

### Viewing the Log

**On Device:**
1. Open a file manager app
2. Navigate to: `Downloads/BoomingMusic/`
3. Open `debug.log` with any text editor

**Via ADB:**
```bash
adb pull /sdcard/Downloads/BoomingMusic/debug.log ./debug.log
```

**From Android Studio:**
1. Open Device File Explorer
2. Navigate to: `/sdcard/Downloads/BoomingMusic/debug.log`
3. Right-click → Save As...

## Performance Considerations

The logger is designed to have minimal impact:

- ✅ **Async-friendly**: Uses synchronized writes to prevent corruption
- ✅ **Append mode**: Doesn't rewrite the entire file
- ✅ **Auto-flush**: Ensures logs are written immediately
- ✅ **Size-limited**: Auto-rotation prevents disk space issues
- ✅ **Debug-only**: Only active in debug builds

### Best Practices

1. **Use appropriate log levels**: Don't log everything as ERROR
2. **Include context**: Add relevant information in messages
3. **Log exceptions**: Always include the full stack trace
4. **Tag consistently**: Use consistent tags for filtering
5. **Clean up old logs**: Periodically delete `debug_old.log`

## Troubleshooting

### Log File Not Created

**Possible causes:**
- Not running debug build (check for `.debug` suffix in app name)
- Storage permissions not granted
- Downloads folder not accessible

**Solution:**
1. Verify you're running the debug build
2. Grant storage permissions in app settings
3. Check Downloads folder exists

### Logs Not Appearing

**Possible causes:**
- Logger not initialized (check App.kt)
- Write failed (check logcat for "DebugLogger" errors)

**Solution:**
1. Check logcat for initialization messages
2. Verify `DebugLogger.initialize()` is called in `App.onCreate()`

### Log File Too Large

**Solution:**
1. Manually delete `debug.log` or `debug_old.log`
2. Reduce logging verbosity in code
3. The auto-rotation should handle this automatically

## Integration Points

The debug logger is integrated into:

- **App.kt**: Initialization, crash handler, StrictMode
- **Network layer**: Ktor/OkHttp interceptors
- **Database**: Room query logging
- **UI**: Activity/Fragment lifecycle
- **Playback**: Player state changes
- **Scanner**: Library scanning progress

## Example Log Output

```
[2026-03-03 10:15:30.123] [SYSTEM] Debug logger initialized - File: /storage/emulated/0/Downloads/BoomingMusic/debug.log
[2026-03-03 10:15:30.125] [SYSTEM] App version: 1.2.1 DEBUG (1210300)
[2026-03-03 10:15:30.126] [SYSTEM] Android version: 13 (API 33)
[2026-03-03 10:15:30.127] [SYSTEM] Device: Google Pixel 7 (proton)
[2026-03-03 10:15:31.456] [LIFECYCLE] [MainActivity] onCreate
[2026-03-03 10:15:31.789] [DATABASE] Query: SELECT * FROM songs | Table: songs | Rows: 1250 | Duration: 45ms | SUCCESS
[2026-03-03 10:15:32.100] [NETWORK] [GET] https://api.lrclib.net/search - Status: 200 - Duration: 320ms - SUCCESS
[2026-03-03 10:15:33.500] [PLAYBACK] Playing: Song Name - Artist
[2026-03-03 10:15:34.200] [PERMISSIONS] Permission: android.permission.READ_MEDIA_AUDIO - GRANTED
[2026-03-03 10:15:35.000] [STRICTMODE] [Thread] Disk read detected on main thread
[2026-03-03 10:15:36.500] [ERROR] [Thread:main] [NetworkRepository] Request failed
java.net.SocketTimeoutException: timeout
    at okhttp3.internal.connection.RealConnection...
[2026-03-03 10:15:37.000] [MEMORY] Memory - Allocated: 256 MB | Used: 128 MB | Total: 512 MB | Usage: 25%
```

## Summary

The debug logging system provides comprehensive visibility into app behavior:

- 📁 **Automatic file logging** to Downloads/BoomingMusic/debug.log
- 🚨 **Crash capture** with full stack traces
- ⚠️ **StrictMode integration** for detecting policy violations
- 📊 **Performance monitoring** for network, database, and UI
- 💾 **Auto-rotation** to prevent disk space issues
- 🔍 **Rich context** including timestamps, threads, and tags

This makes debugging production-like issues much easier without needing to connect to logcat!
