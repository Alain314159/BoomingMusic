package com.mardous.booming.di

import android.content.Context
import com.mardous.booming.data.remote.listenbrainz.api.ListenBrainzApi
import com.mardous.booming.data.remote.listenbrainz.service.ListenBrainzScrobbleService
import com.mardous.booming.playback.listenbrainz.ListenBrainzScrobbleObserver
import com.mardous.booming.ui.screen.settings.listenbrainz.ListenBrainzSettingsViewModel
import com.mardous.booming.work.ListenBrainzSyncWorker
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Módulo Koin para ListenBrainz integration
 */
val listenBrainzModule = module {

    // API Client
    single {
        ListenBrainzApi(get())
    }

    // Service
    single {
        ListenBrainzScrobbleService(
            api = get(),
            credentialsDao = get(),
            queueDao = get(),
            context = androidContext()
        )
    }

    // Observer del player
    factory {
        ListenBrainzScrobbleObserver(get())
    }

    // ViewModel
    viewModel {
        ListenBrainzSettingsViewModel(scrobbleService = get())
    }

    // Worker factory
    factory { (context: Context) ->
        ListenBrainzSyncWorker(context, get())
    }
}
