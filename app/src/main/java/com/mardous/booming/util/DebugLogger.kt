package com.mardous.booming.util

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import com.mardous.booming.BuildConfig
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Debug Logger - Writes logs to a file in Downloads folder for debug builds
 *
 * Features:
 * - Automatic log file creation in Downloads/BoomingMusic/debug.log
 * - Error report generation in Downloads/BoomingMusic/app-errors.md
 * - Logs all severity levels (V, D, I, W, E)
 * - Captures uncaught exceptions and crashes
 * - Includes timestamp, thread, and class information
 * - Automatic log rotation when file exceeds 5MB
 * - Collects all app errors in a structured markdown report
 *
 * Usage:
 *   DebugLogger.v("TAG", "Verbose message")
 *   DebugLogger.d("TAG", "Debug message")
 *   DebugLogger.i("TAG", "Info message")
 *   DebugLogger.w("TAG", "Warning message")
 *   DebugLogger.e("TAG", "Error message", throwable)
 *
 * Files generated:
 *   - Downloads/BoomingMusic/debug.log - Full detailed log
 *   - Downloads/BoomingMusic/app-errors.md - Structured error report
 */
object DebugLogger {

    private const val LOG_DIR = "BoomingMusic"
    private const val LOG_FILE_NAME = "debug.log"
    private const val ERROR_REPORT_FILE_NAME = "app-errors.md"
    private const val MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024 // 5MB
    
    private var isInitialized = false
    private var logFile: File? = null
    private var writer: FileWriter? = null
    private var errorReportFile: File? = null
    private var errorReportWriter: FileWriter? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    private val lock = Any()
    
    // Error counters for summary
    private var errorCount = 0
    private var warningCount = 0
    private var crashCount = 0
    private var strictModeViolationCount = 0
    private var networkErrorCount = 0
    private var databaseErrorCount = 0
    
    /**
     * Initialize the debug logger
     * Call this in Application.onCreate()
     */
    fun initialize(context: Context) {
        if (isInitialized) return

        synchronized(lock) {
            if (isInitialized) return

            try {
                // Only initialize in debug builds
                if (!isDebugBuild()) {
                    return
                }

                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val logDir = File(downloadsDir, LOG_DIR)

                // Create directory if it doesn't exist
                if (!logDir.exists()) {
                    logDir.mkdirs()
                }

                logFile = File(logDir, LOG_FILE_NAME)

                // Rotate log if too large
                if (logFile!!.exists() && logFile!!.length() > MAX_FILE_SIZE_BYTES) {
                    rotateLog()
                }

                writer = FileWriter(logFile, true) // Append mode
                
                // Initialize error report file
                errorReportFile = File(logDir, ERROR_REPORT_FILE_NAME)
                errorReportWriter = FileWriter(errorReportFile!!, false) // Overwrite for fresh report
                writeErrorReportHeader(context)
                
                isInitialized = true

                logInternal("SYSTEM", "Debug logger initialized - File: ${logFile?.absolutePath}")
                logInternal("SYSTEM", "Error report: ${errorReportFile?.absolutePath}")
                logInternal("SYSTEM", "App version: ${getAppVersion(context)}")
                logInternal("SYSTEM", "Android version: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                logInternal("SYSTEM", "Device: ${Build.MANUFACTURER} ${Build.MODEL} (${Build.BOARD})")
                logInternal("SYSTEM", "Build: ${Build.DISPLAY}")
                logInternal("SYSTEM", "CPU ABI: ${Build.SUPPORTED_ABIS.joinToString(", ")}")
                logInternal("SYSTEM", "Available processors: ${Runtime.getRuntime().availableProcessors()}")
                logInternal("SYSTEM", "Max memory: ${formatBytes(Runtime.getRuntime().maxMemory())}")

                // Log storage info
                val externalStorageDir = Environment.getExternalStorageDirectory()
                val totalSpace = externalStorageDir.totalSpace
                val freeSpace = externalStorageDir.freeSpace
                logInternal("SYSTEM", "Storage - Total: ${formatBytes(totalSpace)} | Free: ${formatBytes(freeSpace)}")

            } catch (e: Exception) {
                // If initialization fails, log to Logcat but don't crash
                Log.e("DebugLogger", "Failed to initialize debug logger", e)
            }
        }
    }
    
    /**
     * Log a verbose message
     */
    fun v(tag: String, message: String) {
        if (!isInitialized) return
        Log.v(tag, message)
        logInternal(tag, message, "VERBOSE")
    }
    
    /**
     * Log a debug message
     */
    fun d(tag: String, message: String) {
        if (!isInitialized) return
        Log.d(tag, message)
        logInternal(tag, message, "DEBUG")
    }
    
    /**
     * Log an info message
     */
    fun i(tag: String, message: String) {
        if (!isInitialized) return
        Log.i(tag, message)
        logInternal(tag, message, "INFO")
    }
    
    /**
     * Log a warning message
     */
    fun w(tag: String, message: String) {
        if (!isInitialized) return
        Log.w(tag, message)
        warningCount++
        logInternal(tag, message, "WARNING")
        writeErrorToReport("WARNING", tag, message)
    }

    /**
     * Log a warning message with throwable
     */
    fun w(tag: String, message: String, throwable: Throwable) {
        if (!isInitialized) return
        Log.w(tag, message, throwable)
        warningCount++
        logInternal(tag, "$message - ${throwable.message}", "WARNING", throwable)
        writeErrorToReport("WARNING", tag, "$message - ${throwable.message}", throwable)
    }

    /**
     * Log an error message
     */
    fun e(tag: String, message: String) {
        if (!isInitialized) return
        Log.e(tag, message)
        errorCount++
        logInternal(tag, message, "ERROR")
        writeErrorToReport("ERROR", tag, message)
    }

    /**
     * Log an error message with throwable
     */
    fun e(tag: String, message: String, throwable: Throwable) {
        if (!isInitialized) return
        Log.e(tag, message, throwable)
        errorCount++
        logInternal(tag, "$message - ${throwable.message}", "ERROR", throwable)
        writeErrorToReport("ERROR", tag, "$message - ${throwable.message}", throwable)
    }
    
    /**
     * Log an info message about what's being logged
     */
    fun wtf(tag: String, message: String) {
        if (!isInitialized) return
        Log.wtf(tag, message)
        logInternal(tag, message, "WTF")
    }
    
    /**
     * Log an info message about what's being logged with throwable
     */
    fun wtf(tag: String, message: String, throwable: Throwable) {
        if (!isInitialized) return
        Log.wtf(tag, message, throwable)
        logInternal(tag, "$message - ${throwable.message}", "WTF", throwable)
    }
    
    /**
     * Log a custom exception manually
     */
    fun logException(tag: String, throwable: Throwable, context: String = "") {
        if (!isInitialized) return
        val message = if (context.isNotEmpty()) "$context - ${throwable.message}" else throwable.message ?: "Unknown error"
        Log.e(tag, message, throwable)
        errorCount++
        logInternal(tag, message, "EXCEPTION", throwable)
        writeErrorToReport("EXCEPTION", tag, message, throwable)
    }

    /**
     * Log a network error
     */
    fun logNetworkError(url: String, method: String, statusCode: Int, errorMessage: String) {
        if (!isInitialized) return
        val message = "[$method] $url - Status: $statusCode - $errorMessage"
        networkErrorCount++
        logInternal("NETWORK", message, "ERROR")
        writeErrorToReport("NETWORK ERROR", "NETWORK", message)
    }

    /**
     * Log a database operation
     */
    fun logDatabaseOperation(operation: String, table: String, success: Boolean, details: String = "") {
        if (!isInitialized) return
        val status = if (success) "SUCCESS" else "FAILED"
        val message = "$operation on $table - $status${if (details.isNotEmpty()) " - $details" else ""}"
        if (!success) {
            databaseErrorCount++
            logInternal("DATABASE", message, "ERROR")
            writeErrorToReport("DATABASE ERROR", "DATABASE", message)
        } else {
            logInternal("DATABASE", message, "DEBUG")
        }
    }
    
    /**
     * Log a UI event
     */
    fun logUiEvent(screen: String, event: String, details: String = "") {
        if (!isInitialized) return
        val message = "[$screen] $event${if (details.isNotEmpty()) " - $details" else ""}"
        logInternal("UI", message, "DEBUG")
    }
    
    /**
     * Log a playback event
     */
    fun logPlaybackEvent(event: String, details: String = "") {
        if (!isInitialized) return
        val message = "$event${if (details.isNotEmpty()) " - $details" else ""}"
        logInternal("PLAYBACK", message, "INFO")
    }
    
    /**
     * Log a scanner event
     */
    fun logScannerEvent(event: String, details: String = "") {
        if (!isInitialized) return
        val message = "$event${if (details.isNotEmpty()) " - $details" else ""}"
        logInternal("SCANNER", message, "INFO")
    }
    
    /**
     * Log a crash/exception with full details
     */
    fun logCrash(throwable: Throwable, context: String = "CRASH") {
        if (!isInitialized) return
        crashCount++
        logInternal(context, "FATAL EXCEPTION: ${throwable.message}", "FATAL", throwable)
        writeErrorToReport("CRASH", context, "FATAL EXCEPTION: ${throwable.message}", throwable)
    }

    /**
     * Log a StrictMode violation
     */
    fun logStrictModeViolation(policyType: String, message: String, throwable: Throwable? = null) {
        if (!isInitialized) return
        strictModeViolationCount++
        logInternal("STRICTMODE", "[$policyType] $message", "WARNING", throwable)
        writeErrorToReport("STRICTMODE VIOLATION", "STRICTMODE", "[$policyType] $message", throwable)
    }

    /**
     * Log a network request with timing
     */
    fun logNetworkRequest(url: String, method: String, startTime: Long, endTime: Long, statusCode: Int, error: String? = null) {
        if (!isInitialized) return
        val duration = endTime - startTime
        val status = if (error == null) "SUCCESS" else "FAILED"
        val message = "[$method] $url - Status: $statusCode - Duration: ${duration}ms - $status${if (error != null) " - $error" else ""}"
        logInternal("NETWORK", message, if (error == null) "DEBUG" else "ERROR")
    }

    /**
     * Log a database query with performance info
     */
    fun logDatabaseQuery(query: String, table: String, durationMs: Long, rowCount: Int, success: Boolean) {
        if (!isInitialized) return
        val status = if (success) "SUCCESS" else "FAILED"
        val message = "Query: $query | Table: $table | Rows: $rowCount | Duration: ${durationMs}ms | $status"
        logInternal("DATABASE", message, if (success && durationMs < 100) "DEBUG" else "WARNING")
    }

    /**
     * Log memory usage
     */
    fun logMemoryUsage(allocatedBytes: Long, freeBytes: Long, totalBytes: Long) {
        if (!isInitialized) return
        val usedBytes = totalBytes - freeBytes
        val usagePercent = (usedBytes.toDouble() / totalBytes.toDouble()) * 100
        val message = "Memory - Allocated: ${formatBytes(allocatedBytes)} | Used: ${formatBytes(usedBytes)} | Total: ${formatBytes(totalBytes)} | Usage: ${usagePercent.toInt()}%"
        logInternal("MEMORY", message, "INFO")
    }

    /**
     * Log a lifecycle event
     */
    fun logLifecycleEvent(component: String, event: String) {
        if (!isInitialized) return
        logInternal("LIFECYCLE", "[$component] $event", "DEBUG")
    }

    /**
     * Log a permission result
     */
    fun logPermissionResult(permission: String, granted: Boolean, rationale: Boolean = false) {
        if (!isInitialized) return
        val status = if (granted) "GRANTED" else "DENIED${if (rationale) " (show rationale)" else ""}"
        logInternal("PERMISSIONS", "Permission: $permission - $status", if (granted) "DEBUG" else "WARNING")
    }

    /**
     * Log a background task execution
     */
    fun logBackgroundTask(taskName: String, durationMs: Long, success: Boolean, error: String? = null) {
        if (!isInitialized) return
        val status = if (success) "COMPLETED" else "FAILED"
        val message = "Task: $taskName | Duration: ${durationMs}ms | $status${if (error != null) " - $error" else ""}"
        logInternal("WORKER", message, if (success) "DEBUG" else "ERROR")
    }

    /**
     * Log UI rendering performance
     */
    fun logUiRendering(component: String, renderTimeMs: Long, frameDrop: Boolean) {
        if (!isInitialized) return
        val severity = when {
            renderTimeMs < 16 -> "DEBUG"
            renderTimeMs < 100 -> "WARNING"
            else -> "ERROR"
        }
        val message = "[$component] Render time: ${renderTimeMs}ms${if (frameDrop) " - FRAME DROPPED" else ""}"
        logInternal("UI_RENDER", message, severity)
    }

    /**
     * Log a configuration change
     */
    fun logConfigurationChange(changeType: String, oldValue: String, newValue: String) {
        if (!isInitialized) return
        val message = "$changeType changed from '$oldValue' to '$newValue'"
        logInternal("CONFIG", message, "INFO")
    }

    /**
     * Flush the log writer
     */
    fun flush() {
        synchronized(lock) {
            writer?.flush()
        }
    }
    
    /**
     * Close the log writer
     * Call this when shutting down
     */
    fun close() {
        synchronized(lock) {
            try {
                // Update error report summary before closing
                updateErrorReportSummary()
                
                // Write final summary
                writeErrorReportFooter()
                
                // Close writers
                writer?.close()
                writer = null
                
                errorReportWriter?.close()
                errorReportWriter = null
                
                isInitialized = false
                
                Log.d("DebugLogger", "Logger closed. Errors: $errorCount, Warnings: $warningCount, Crashes: $crashCount")
            } catch (e: Exception) {
                Log.e("DebugLogger", "Failed to close logger", e)
            }
        }
    }
    
    /**
     * Get the current log file path
     */
    fun getLogFilePath(): String? {
        return logFile?.absolutePath
    }

    /**
     * Get the error report file path
     */
    fun getErrorReportFilePath(): String? {
        return errorReportFile?.absolutePath
    }

    /**
     * Check if logger is initialized
     */
    fun isInitialized(): Boolean {
        return isInitialized
    }

    /**
     * Get current error count
     */
    fun getErrorCount(): Int = errorCount

    /**
     * Get current warning count
     */
    fun getWarningCount(): Int = warningCount

    /**
     * Get current crash count
     */
    fun getCrashCount(): Int = crashCount

    // Private methods

    private fun writeErrorReportHeader(context: Context) {
        try {
            errorReportWriter?.write("""
# 🐛 Booming Music - Error Report

> **Generated:** ${getCurrentTimestamp()}
> **App Version:** ${getAppVersion(context)}
> **Android:** ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})
> **Device:** ${Build.MANUFACTURER} ${Build.MODEL}
> **Build:** ${Build.DISPLAY}

---

## 📊 Summary

| Type | Count |
|------|-------|
| Errors | 0 |
| Warnings | 0 |
| Crashes | 0 |
| StrictMode Violations | 0 |
| Network Errors | 0 |
| Database Errors | 0 |

---

## 📝 Error Log

""".trimIndent())
            errorReportWriter?.flush()
        } catch (e: Exception) {
            Log.e("DebugLogger", "Failed to write error report header", e)
        }
    }

    private fun writeErrorToReport(level: String, tag: String, message: String, throwable: Throwable? = null) {
        try {
            val timestamp = getCurrentTimestamp()
            val errorEntry = buildString {
                append("### [$level] $tag\n\n")
                append("**Time:** $timestamp\n\n")
                append("**Message:** $message\n\n")

                if (throwable != null) {
                    append("**Exception:** ${throwable.javaClass.simpleName}\n\n")
                    append("**Stack Trace:**\n")
                    append("```\n")
                    append(getStackTrace(throwable))
                    append("\n```\n\n")
                }

                append("---\n\n")
            }

            errorReportWriter?.write(errorEntry)
            errorReportWriter?.flush()
        } catch (e: Exception) {
            Log.e("DebugLogger", "Failed to write error to report", e)
        }
    }

    private fun updateErrorReportSummary() {
        try {
            // Read existing content
            val existingContent = errorReportFile?.readText() ?: return

            // Create updated summary
            val updatedContent = existingContent
                .replace("| Errors | [0-9]+ |".toRegex(), "| Errors | $errorCount |")
                .replace("| Warnings | [0-9]+ |".toRegex(), "| Warnings | $warningCount |")
                .replace("| Crashes | [0-9]+ |".toRegex(), "| Crashes | $crashCount |")
                .replace("| StrictMode Violations | [0-9]+ |".toRegex(), "| StrictMode Violations | $strictModeViolationCount |")
                .replace("| Network Errors | [0-9]+ |".toRegex(), "| Network Errors | $networkErrorCount |")
                .replace("| Database Errors | [0-9]+ |".toRegex(), "| Database Errors | $databaseErrorCount |")

            // Rewrite file
            FileWriter(errorReportFile!!).use { writer ->
                writer.write(updatedContent)
                writer.flush()
            }
        } catch (e: Exception) {
            Log.e("DebugLogger", "Failed to update error report summary", e)
        }
    }

    private fun writeErrorReportFooter() {
        try {
            val timestamp = getCurrentTimestamp()
            val footer = """

---

## 📊 Final Summary

**Session Ended:** $timestamp

| Type | Total Count |
|------|-------------|
| Errors | $errorCount |
| Warnings | $warningCount |
| Crashes | $crashCount |
| StrictMode Violations | $strictModeViolationCount |
| Network Errors | $networkErrorCount |
| Database Errors | $databaseErrorCount |

---

> **Log file:** `${logFile?.absolutePath}`
> **Error report:** `${errorReportFile?.absolutePath}`

---

*This report was automatically generated by Booming Music Debug Logger*
"""
            errorReportWriter?.write(footer)
            errorReportWriter?.flush()
        } catch (e: Exception) {
            Log.e("DebugLogger", "Failed to write error report footer", e)
        }
    }

    private fun getCurrentTimestamp(): String {
        return dateFormat.format(Date())
    }

    // Private methods
    
    private fun logInternal(tag: String, message: String, level: String = "DEBUG", throwable: Throwable? = null) {
        synchronized(lock) {
            if (!isInitialized || writer == null) return
            
            try {
                val timestamp = dateFormat.format(Date())
                val threadName = Thread.currentThread().name
                val logLine = buildString {
                    append("[$timestamp]")
                    append(" [$level]")
                    append(" [Thread:$threadName]")
                    append(" [$tag]")
                    append(" $message")
                    
                    if (throwable != null) {
                        append("\n")
                        append(getStackTrace(throwable))
                    }
                    
                    append("\n")
                }
                
                writer?.write(logLine)
                writer?.flush()
                
                // Rotate if exceeds size after writing
                if (logFile?.exists() == true && logFile!!.length() > MAX_FILE_SIZE_BYTES) {
                    rotateLog()
                }
                
            } catch (e: Exception) {
                // Don't crash if logging fails
                Log.e("DebugLogger", "Failed to write log", e)
            }
        }
    }
    
    private fun getStackTrace(throwable: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        return sw.toString()
    }
    
    private fun rotateLog() {
        try {
            logFile?.renameTo(File(logFile?.parentFile, "debug_old.log"))
            logFile?.delete()
        } catch (e: Exception) {
            Log.e("DebugLogger", "Failed to rotate log file", e)
        }
    }
    
    private fun formatBytes(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> "${bytes / (1024 * 1024 * 1024)} GB"
        }
    }

    private fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }
    
    private fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.versionCode})"
        } catch (e: Exception) {
            "Unknown"
        }
    }
}
