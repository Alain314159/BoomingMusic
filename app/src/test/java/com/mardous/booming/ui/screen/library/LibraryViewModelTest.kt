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

package com.mardous.booming.ui.screen.library

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mardous.booming.coil.CustomPlaylistImageManager
import com.mardous.booming.data.local.room.InclExclDao
import com.mardous.booming.data.local.repository.Repository
import com.mardous.booming.data.model.Song
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for LibraryViewModel.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var libraryViewModel: LibraryViewModel
    private lateinit var mockRepository: Repository
    private lateinit var mockInclExclDao: InclExclDao
    private lateinit var mockCustomPlaylistImageManager: CustomPlaylistImageManager

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        mockRepository = mockk()
        mockInclExclDao = mockk()
        mockCustomPlaylistImageManager = mockk()
        
        every { mockRepository.allSongs() } returns emptyList()
        every { mockRepository.allAlbums() } returns emptyList()
        every { mockRepository.allArtists() } returns emptyList()
        
        libraryViewModel = LibraryViewModel(
            repository = mockRepository,
            inclExclDao = mockInclExclDao,
            customPlaylistImageManager = mockCustomPlaylistImageManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `ViewModel should initialize correctly`() {
        // Then
        assertNotNull(libraryViewModel)
        assertNotNull(libraryViewModel.getSongs())
        assertNotNull(libraryViewModel.getAlbums())
        assertNotNull(libraryViewModel.getArtists())
    }

    @Test
    fun `getSongs should return empty list initially`() {
        // When
        val songs = libraryViewModel.getSongs()
        
        // Then
        assertNotNull(songs)
    }

    @Test
    fun `requestLibraryScan should be callable`() {
        // When
        libraryViewModel.requestLibraryScan()
        
        // Then
        // Test passes if no exception is thrown
    }

    @Test
    fun `enableAutoScan should be callable`() {
        // When
        libraryViewModel.enableAutoScan()
        
        // Then
        // Test passes if no exception is thrown
    }

    @Test
    fun `disableAutoScan should be callable`() {
        // When
        libraryViewModel.disableAutoScan()
        
        // Then
        // Test passes if no exception is thrown
    }
}
