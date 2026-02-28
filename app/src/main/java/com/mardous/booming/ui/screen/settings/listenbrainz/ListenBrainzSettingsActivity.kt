package com.mardous.booming.ui.screen.settings.listenbrainz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mardous.booming.ui.theme.BoomingMusicTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity para configuración de ListenBrainz
 */
@AndroidEntryPoint
class ListenBrainzSettingsActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            BoomingMusicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ListenBrainzSettingsScreen(
                        onNavigateBack = { finish() }
                    )
                }
            }
        }
    }
}
