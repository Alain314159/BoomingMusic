package com.mardous.booming.data.remote.listenbrainz.service

import android.content.Context
import com.mardous.booming.data.local.room.dao.ListenBrainzCredentialsDao
import com.mardous.booming.data.local.room.dao.ListenBrainzScrobbleQueueDao
import com.mardous.booming.data.local.room.entity.ListenBrainzScrobbleQueueEntity
import com.mardous.booming.data.remote.listenbrainz.api.ListenBrainzApi
import com.mardous.booming.data.remote.listenbrainz.model.ListenBrainzCredentials
import com.mardous.booming.data.remote.listenbrainz.model.ListenBrainzScrobble
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Servicio para gestionar scrobbles a ListenBrainz
 * 
 * Maneja:
 * - Envío de scrobbles individuales
 * - Cola de scrobbles pendientes (offline)
 * - Now Playing updates
 * - Reintentos automáticos
 */
@Singleton
class ListenBrainzScrobbleService @Inject constructor(
    private val api: ListenBrainzApi,
    private val credentialsDao: ListenBrainzCredentialsDao,
    private val queueDao: ListenBrainzScrobbleQueueDao,
    private val context: Context
) {
    
    /**
     * Obtiene el estado de autenticación como Flow
     */
    fun getAuthStatus(): Flow<AuthState> {
        return credentialsDao.getCredentialsFlow()
            .map { credentials ->
                if (credentials != null && credentials.isLoggedIn) {
                    AuthState.LoggedIn(credentials.username ?: "User", credentials.userToken)
                } else {
                    AuthState.NotLoggedIn
                }
            }
    }
    
    /**
     * Obtiene credenciales actuales (suspend)
     */
    suspend fun getCurrentCredentials(): ListenBrainzCredentials? {
        return credentialsDao.getCredentialsFlow().first()?.toDomain()
    }
    
    /**
     * Guarda credenciales después de validar token
     */
    suspend fun saveCredentials(token: String, username: String?) {
        val credentials = ListenBrainzCredentials.create(token)
        val entity = ListenBrainzCredentialsEntity.fromDomain(credentials)
            .copy(username = username)
        credentialsDao.saveCredentials(entity)
    }
    
    /**
     * Valida token y guarda credenciales si es válido
     */
    suspend fun validateAndSaveToken(token: String): Result<String> {
        return runCatching {
            // Validar token con API
            val response = api.validateToken(token).getOrThrow()
            
            if (response.status == "ok" && response.userName != null) {
                // Token válido, guardar credenciales
                saveCredentials(token, response.userName)
                response.userName
            } else {
                throw Exception(response.error ?: "Invalid token")
            }
        }
    }
    
    /**
     * Cierra sesión y limpia credenciales
     */
    suspend fun logout() {
        credentialsDao.clearCredentials()
        queueDao.clearAll() // Limpiar cola pendiente
    }
    
    /**
     * Verifica si el usuario está logueado
     */
    suspend fun isLoggedIn(): Boolean {
        return credentialsDao.getCredentialsFlow().first()?.isLoggedIn == true
    }
    
    /**
     * Obtiene el user token actual (si está logueado)
     */
    suspend fun getUserToken(): String? {
        return credentialsDao.getCredentialsFlow().first()?.userToken
    }
    
    /**
     * Obtiene el username actual
     */
    suspend fun getUsername(): String? {
        return credentialsDao.getCredentialsFlow().first()?.username
    }
    
    /**
     * Envía un scrobble a ListenBrainz
     * 
     * Si hay conexión → envía inmediatamente
     * Si NO hay conexión → guarda en cola
     */
    suspend fun submitScrobble(scrobble: ListenBrainzScrobble) {
        val token = getUserToken() ?: return
        
        try {
            // Intentar enviar inmediatamente
            val response = api.scrobble(token, scrobble)
            
            response.onFailure { error ->
                // Error de red u otro, guardar en cola
                queueScrobble(scrobble)
            }
        } catch (e: Exception) {
            // Error, guardar en cola
            queueScrobble(scrobble)
        }
    }
    
    /**
     * Actualiza estado "Now Playing"
     */
    suspend fun updateNowPlaying(scrobble: ListenBrainzScrobble) {
        val token = getUserToken() ?: return
        
        try {
            api.updateNowPlaying(token, scrobble)
        } catch (e: Exception) {
            // Now playing no es crítico, solo loguear
            e.printStackTrace()
        }
    }
    
    /**
     * Agrega un scrobble a la cola pendiente
     */
    private suspend fun queueScrobble(scrobble: ListenBrainzScrobble) {
        val entity = ListenBrainzScrobbleQueueEntity.fromScrobble(scrobble)
        queueDao.insert(entity)
    }
    
    /**
     * Obtiene scrobbles pendientes de la cola
     */
    suspend fun getPendingScrobbles(): List<ListenBrainzScrobbleQueueEntity> {
        return queueDao.getAllPendingSync()
    }
    
    /**
     * Procesa la cola de scrobbles pendientes
     * 
     * Se llama desde WorkManager cuando hay conexión
     */
    suspend fun processQueue(): ScrobbleQueueResult {
        val token = getUserToken()
        if (token == null) {
            return ScrobbleQueueResult.NotLoggedIn
        }
        
        val pendingScrobbles = getPendingScrobbles()
        if (pendingScrobbles.isEmpty()) {
            return ScrobbleQueueResult.Success(0, 0)
        }
        
        var successCount = 0
        var failCount = 0
        
        for (scrobbleEntity in pendingScrobbles) {
            try {
                val response = api.scrobble(token, scrobbleEntity.toScrobble())
                
                response.onSuccess {
                    // Éxito, eliminar de la cola
                    queueDao.delete(scrobbleEntity)
                    successCount++
                }.onFailure {
                    // Error, incrementar reintentos
                    if (scrobbleEntity.retryCount >= MAX_RETRIES) {
                        // Máximo de reintentos alcanzado, eliminar
                        queueDao.delete(scrobbleEntity)
                    } else {
                        // Reintentar luego
                        queueDao.incrementRetryCount(scrobbleEntity.id)
                    }
                    failCount++
                }
            } catch (e: Exception) {
                failCount++
            }
        }
        
        return ScrobbleQueueResult.Success(successCount, failCount)
    }
    
    /**
     * Cuenta scrobbles pendientes
     */
    suspend fun getPendingCount(): Int {
        return queueDao.getPendingCount()
    }
    
    companion object {
        private const val MAX_RETRIES = 5
    }
}

/**
 * Estados de autenticación
 */
sealed class AuthState {
    object NotLoggedIn : AuthState()
    data class LoggedIn(val username: String, val userToken: String) : AuthState()
}

/**
 * Resultado del procesamiento de cola
 */
sealed class ScrobbleQueueResult {
    object NotLoggedIn : ScrobbleQueueResult()
    data class Success(val sentCount: Int, val failedCount: Int) : ScrobbleQueueResult()
}
