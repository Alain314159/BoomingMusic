package com.mardous.booming.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Manager para programar trabajos periódicos de ListenBrainz
 */
object ListenBrainzWorkManager {

    private const val WORK_NAME = "listenbrainz_sync"
    private const val TAG_SYNC = "listenbrainz"

    /**
     * Programa sync periódico de scrobbles
     * Se ejecuta cada 15 minutos si hay scrobbles pendientes
     */
    fun schedulePeriodicSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .build()

        // Sync cada 15 minutos
        val workRequest = PeriodicWorkRequestBuilder<ListenBrainzSyncWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(TAG_SYNC)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    /**
     * Cancela sync periódico
     */
    fun cancelPeriodicSync(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    /**
     * Ejecuta sync inmediato (manual desde settings)
     */
    fun syncNow(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ListenBrainzSyncWorker>()
            .setConstraints(constraints)
            .addTag(TAG_SYNC)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
