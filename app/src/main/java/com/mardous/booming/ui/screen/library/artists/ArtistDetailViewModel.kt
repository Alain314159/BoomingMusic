package com.mardous.booming.ui.screen.library.artists

import androidx.lifecycle.*
import com.mardous.booming.data.local.repository.Repository
import com.mardous.booming.data.model.Artist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArtistDetailViewModel(
    private val repository: Repository,
    private val artistId: Long,
    private val artistName: String?
) : ViewModel() {

    private val _artistDetail = MutableLiveData<Artist>()

    fun getArtist() = getArtistDetail().value ?: Artist.empty

    fun getArtistDetail(): LiveData<Artist> = _artistDetail

    fun loadArtistDetail() = viewModelScope.launch(Dispatchers.IO) {
        val artist = if (!artistName.isNullOrEmpty()) {
            repository.albumArtistByName(artistName)
        } else if (artistId != -1L) {
            repository.artistById(artistId)
        } else {
            Artist.empty
        }

        // Validate artist before posting to UI
        if (artist == Artist.empty || (artist.albums.isEmpty() && artist.songs.isEmpty())) {
            _artistDetail.postValue(Artist.empty)
        } else {
            _artistDetail.postValue(artist)
        }
    }

    fun getSimilarArtists(artist: Artist): LiveData<List<Artist>> = liveData(Dispatchers.IO) {
        emit(repository.similarAlbumArtists(artist).sortedBy { it.name })
    }

    // Eliminado: getArtistBio() - Last.fm removido, usar ListenBrainz en su lugar
}