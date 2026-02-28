package com.mardous.booming.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.Constraints
import androidx.work.NetworkType
import com.mardous.booming.work.ListenBrainzSyncWorker
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

/**
 * Módulo Koin para configuración de WorkManager
 */
val workManagerModule = module {
    single {
        // Configurar WorkManager
        val workManager = WorkManager.getInstance(get<Context>())
        
        // Programar sync periódico de ListenBrainz (cada 15 minutos si hay conexión)
        val syncWork = PeriodicWorkRequestBuilder<ListenBrainzSyncWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            "listenbrainz_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncWork
        )
        
        workManager
    }
}
