package com.mardous.booming.ui.screen.settings.listenbrainz

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mardous.booming.data.remote.listenbrainz.service.AuthState
import org.koin.androidx.compose.koinViewModel

/**
 * Pantalla de configuración de ListenBrainz
 */
@Composable
fun ListenBrainzSettingsScreen(
    viewModel: ListenBrainzSettingsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ListenBrainz") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Abrir perfil de ListenBrainz
                        if (uiState is AuthState.LoggedIn) {
                            val username = (uiState as AuthState.LoggedIn).username
                            val url = "https://listenbrainz.org/user/$username"
                            val customTabsIntent = CustomTabsIntent.Builder().build()
                            customTabsIntent.launchUrl(context, Uri.parse(url))
                        }
                    }) {
                        Icon(Icons.Default.OpenInNew, contentDescription = "View Profile")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is AuthState.NotLoggedIn -> {
                    ListenBrainzLoginCard(
                        onTokenSubmit = viewModel::validateToken,
                        onHelpClick = {
                            // Abrir guía de ayuda
                            val customTabsIntent = CustomTabsIntent.Builder().build()
                            customTabsIntent.launchUrl(
                                context,
                                Uri.parse("https://listenbrainz.org/settings")
                            )
                        }
                    )
                }
                is AuthState.LoggedIn -> {
                    ListenBrainzStatusCard(
                        username = state.username,
                        onDisconnect = viewModel::logout
                    )
                }
            }
        }
    }
}

/**
 * Card de login para ListenBrainz
 */
@Composable
fun ListenBrainzLoginCard(
    onTokenSubmit: (String) -> Unit,
    onHelpClick: () -> Unit
) {
    var token by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Connect to ListenBrainz",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Track your music listening history",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Campo de token
            OutlinedTextField(
                value = token,
                onValueChange = { 
                    token = it
                    isError = false
                },
                label = { Text("User Token") },
                placeholder = { Text("a1b2c3d4-e5f6-7890-...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = isError,
                supportingText = if (isError) {{ Text(errorMessage) }} else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                keyboardActions = KeyboardActions(
                    onDone = { onTokenSubmit(token.trim()) }
                ),
                leadingIcon = {
                    Icon(Icons.Default.Key, contentDescription = null)
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón de conectar
            Button(
                onClick = { onTokenSubmit(token.trim()) },
                modifier = Modifier.fillMaxWidth(),
                enabled = token.isNotBlank()
            ) {
                Icon(Icons.Default.Link, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Connect")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Enlace de ayuda
            TextButton(onClick = onHelpClick) {
                Icon(Icons.Default.Help, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Get token from ListenBrainz")
            }
        }
    }
}

/**
 * Card de estado cuando está conectado
 */
@Composable
fun ListenBrainzStatusCard(
    username: String,
    onDisconnect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Connected!",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Logged in as: $username",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Información
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoItem(
                    icon = Icons.Default.MusicNote,
                    label = "Scrobbling",
                    value = "Active"
                )
                
                InfoItem(
                    icon = Icons.Default.Cloud,
                    label = "Sync",
                    value = "Auto"
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón de desconectar
            OutlinedButton(
                onClick = onDisconnect,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.LinkOff, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Disconnect")
            }
        }
    }
}

@Composable
fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
