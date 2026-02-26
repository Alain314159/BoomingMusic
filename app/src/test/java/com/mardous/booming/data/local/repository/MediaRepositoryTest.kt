/*
 * Copyright (c) 2024 Christians Martínez Alvarado
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mardous.booming.data.local.repository

import com.mardous.booming.data.local.room.ScannedMediaCache
import com.mardous.booming.data.local.room.ScannedMediaCacheDao
import com.mardous.booming.data.model.Song
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for MediaRepository.
 */
class MediaRepositoryTest {

    private lateinit var mediaRepository: MediaRepository
    private lateinit var mockCacheDao: ScannedMediaCacheDao
    private lateinit var mockSongRepository: SongRepository

    @Before
    fun setup() {
        mockCacheDao = mockk()
        mockSongRepository = mockk()
        
        // Nota: En producción, usar Koin para inyección correcta
        // Esto es una simplificación para el test
        mediaRepository = MediaRepository()
    }

    @Test
    fun `toSong should convert ScannedMediaCache to Song correctly`() = runTest {
        // Given
        val cache = ScannedMediaCache(
            cacheId = 1,
            filePath = "/storage/music/song.mp3",
            fileName = "song.mp3",
            fileSize = 5000000,
            lastModified = System.currentTimeMillis(),
            title = "Test Song",
            artist = "Test Artist",
            album = "Test Album",
            albumArtist = "Test Artist",
            genre = "Rock",
            year = 2024,
            trackNumber = 1,
            duration = 240000,
            bitrate = 320,
            sampleRate = 44100,
            scanTimestamp = System.currentTimeMillis(),
            mediaStoreId = null,
            isValid = true
        )
        
        // When
        val song = cache.toSong()
        
        // Then
        assertNotNull(song)
        assertEquals("Test Song", song?.title)
        assertEquals("Test Artist", song?.artistName)
        assertEquals("Test Album", song?.albumName)
        assertEquals(240000L, song?.duration)
    }

    @Test
    fun `toSong should return null for invalid cache entry`() = runTest {
        // Given
        val cache = ScannedMediaCache(
            cacheId = 1,
            filePath = "/storage/music/song.mp3",
            fileName = "",  // Empty filename
            fileSize = 0,
            lastModified = 0,
            title = null,
            artist = null,
            album = null,
            albumArtist = null,
            genre = null,
            year = null,
            trackNumber = null,
            duration = null,
            bitrate = null,
            sampleRate = null,
            scanTimestamp = 0,
            mediaStoreId = null,
            isValid = false
        )
        
        // When
        val song = cache.toSong()
        
        // Then
        assertEquals(null, song)
    }

    @Test
    fun `toSong should use fileName when title is null`() = runTest {
        // Given
        val cache = ScannedMediaCache(
            cacheId = 1,
            filePath = "/storage/music/song.mp3",
            fileName = "song.mp3",
            fileSize = 5000000,
            lastModified = System.currentTimeMillis(),
            title = null,  // No title
            artist = "Test Artist",
            album = null,
            albumArtist = null,
            genre = null,
            year = null,
            trackNumber = null,
            duration = 240000L,
            bitrate = null,
            sampleRate = null,
            scanTimestamp = System.currentTimeMillis(),
            mediaStoreId = null,
            isValid = true
        )
        
        // When
        val song = cache.toSong()
        
        // Then
        assertNotNull(song)
        assertEquals("song.mp3", song?.title)
    }
}
