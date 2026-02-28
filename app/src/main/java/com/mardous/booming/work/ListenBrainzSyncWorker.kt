package com.mardous.booming.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mardous.booming.data.remote.listenbrainz.service.ListenBrainzScrobbleService
import com.mardous.booming.data.remote.listenbrainz.service.ScrobbleQueueResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker para sincronizar scrobbles pendientes con ListenBrainz
 * 
 * Se ejecuta:
 * - Periódicamente (cada 15 min si hay scrobbles pendientes)
 * - Cuando se recupera conexión a internet
 * - Manualmente desde settings
 */
@HiltWorker
class ListenBrainzSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val scrobbleService: ListenBrainzScrobbleService
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // Verificar si está logueado
            if (!scrobbleService.isLoggedIn()) {
                return Result.success() // No hacer nada si no está logueado
            }
            
            // Procesar cola de scrobbles
            val result = scrobbleService.processQueue()
            
            when (result) {
                is ScrobbleQueueResult.NotLoggedIn -> {
                    Result.success()
                }
                is ScrobbleQueueResult.Success -> {
                    if (result.sentCount > 0) {
                        // Log de éxito
                        android.util.Log.d(
                            "ListenBrainzSync",
                            "Sync completo: ${result.sentCount} enviados, ${result.failedCount} fallidos"
                        )
                    }
                    
                    if (result.failedCount > 0) {
                        // Algunos fallaron, reintentar luego
                        Result.retry()
                    } else {
                        Result.success()
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ListenBrainzSync", "Error en sync", e)
            Result.retry()
        }
    }
}
