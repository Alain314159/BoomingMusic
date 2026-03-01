package com.mardous.booming.ui.screen.settings.listenbrainz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mardous.booming.data.remote.listenbrainz.service.AuthState
import com.mardous.booming.data.remote.listenbrainz.service.ListenBrainzScrobbleService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de settings de ListenBrainz
 */
class ListenBrainzSettingsViewModel(
    private val scrobbleService: ListenBrainzScrobbleService
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthState>(AuthState.NotLoggedIn)
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    init {
        // Observar estado de autenticación
        viewModelScope.launch {
            scrobbleService.getAuthStatus().collect { state ->
                _uiState.value = state
            }
        }
    }

    /**
     * Valida el token ingresado por el usuario
     */
    fun validateToken(token: String) {
        if (token.isBlank()) {
            // Token vacío, mostrar error
            _uiState.value = AuthState.NotLoggedIn
            return
        }

        viewModelScope.launch {
            try {
                val result = scrobbleService.validateAndSaveToken(token)

                result.onSuccess { username ->
                    // Éxito, actualizar estado
                    _uiState.value = AuthState.LoggedIn(username, token)
                }.onFailure { error ->
                    // Error, mostrar mensaje
                    _uiState.value = AuthState.NotLoggedIn
                    // TODO: Mostrar error en UI
                }
            } catch (e: Exception) {
                // Error de validación
                _uiState.value = AuthState.NotLoggedIn
            }
        }
    }

    /**
     * Cierra sesión y elimina credenciales
     */
    fun logout() {
        viewModelScope.launch {
            scrobbleService.logout()
            _uiState.value = AuthState.NotLoggedIn
        }
    }
}
