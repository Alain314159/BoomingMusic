package com.mardous.booming.ui.screen.library.albums

import androidx.lifecycle.*
import com.mardous.booming.data.local.repository.Repository
import com.mardous.booming.data.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlbumDetailViewModel(private val repository: Repository, private val albumId: Long) : ViewModel() {

    private val _albumDetail = MutableLiveData<Album>()

    fun getAlbumDetail(): LiveData<Album> = _albumDetail

    fun getAlbum() = getAlbumDetail().value ?: Album.Companion.empty

    fun loadAlbumDetail() = viewModelScope.launch(Dispatchers.IO) {
        _albumDetail.postValue(repository.albumById(albumId))
    }

    fun getSimilarAlbums(album: Album): LiveData<List<Album>> = liveData(Dispatchers.IO) {
        repository.similarAlbums(album).let {
            if (it.isNotEmpty()) emit(it)
        }
    }
}