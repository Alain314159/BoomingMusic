/*
 * Copyright (c) 2025 Christians Martínez Alvarado
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

package com.mardous.booming.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mardous.booming.core.BoomingDatabase
import com.mardous.booming.data.local.room.SongArtistEntity
import com.mardous.booming.data.local.room.SongEntity
import com.mardous.booming.data.local.room.dao.SongArtistDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Tests unitarios para SongArtistDao
 * 
 * Verifica la gestión de la relación muchos-a-muchos entre canciones y artistas
 */
@RunWith(AndroidJUnit4::class)
class SongArtistDaoTest {

    private lateinit var songArtistDao: SongArtistDao
    private lateinit var db: BoomingDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, BoomingDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        songArtistDao = db.songArtistDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertArtistForSong_andGetArtists() = runTest {
        // Given: Una canción con ID 1
        val songId = 1L
        
        // When: Agregar múltiples artistas
        songArtistDao.insert(SongArtistEntity.create(songId, "Artista Principal", 0))
        songArtistDao.insert(SongArtistEntity.create(songId, "Feature 1", 1))
        songArtistDao.insert(SongArtistEntity.create(songId, "Feature 2", 2))
        
        // Then: Obtener artistas en orden correcto
        val artists = songArtistDao.getArtistsForSong(songId)
        Assert.assertEquals(3, artists.size)
        Assert.assertEquals("Artista Principal", artists[0].artistName)
        Assert.assertEquals("Feature 1", artists[1].artistName)
        Assert.assertEquals("Feature 2", artists[2].artistName)
    }

    @Test
    @Throws(Exception::class)
    fun deleteArtistFromSong() = runTest {
        // Given: Canción con 3 artistas
        val songId = 2L
        songArtistDao.insert(SongArtistEntity.create(songId, "Artist A", 0))
        songArtistDao.insert(SongArtistEntity.create(songId, "Artist B", 1))
        songArtistDao.insert(SongArtistEntity.create(songId, "Artist C", 2))
        
        // When: Eliminar artista del medio
        val artistB = songArtistDao.getArtistsForSong(songId).find { it.artistName == "Artist B" }!!
        songArtistDao.delete(artistB)
        
        // Then: Solo quedan 2 artistas
        val remainingArtists = songArtistDao.getArtistsForSong(songId)
        Assert.assertEquals(2, remainingArtists.size)
        Assert.assertTrue(remainingArtists.none { it.artistName == "Artist B" })
    }

    @Test
    @Throws(Exception::class)
    fun updateArtistOrder() = runTest {
        // Given: Artistas en orden A, B, C
        val songId = 3L
        songArtistDao.insert(SongArtistEntity.create(songId, "Artist A", 0))
        songArtistDao.insert(SongArtistEntity.create(songId, "Artist B", 1))
        songArtistDao.insert(SongArtistEntity.create(songId, "Artist C", 2))
        
        // When: Cambiar orden de Artist C a posición 0
        songArtistDao.updateArtistOrder(songId, "Artist C", 0)
        
        // Then: Verificar nuevo orden
        val artists = songArtistDao.getArtistsForSong(songId)
        Assert.assertEquals("Artist C", artists.first { it.artistOrder == 0 }.artistName)
    }

    @Test
    @Throws(Exception::class)
    fun getSongsForArtist() = runTest {
        // Given: Múltiples canciones con el mismo artista
        songArtistDao.insert(SongArtistEntity.create(10L, "Common Artist", 0))
        songArtistDao.insert(SongArtistEntity.create(11L, "Common Artist", 0))
        songArtistDao.insert(SongArtistEntity.create(12L, "Other Artist", 0))
        
        // When: Buscar canciones por artista
        val songs = songArtistDao.getSongsForArtist("Common Artist")
        
        // Then: Debe retornar 2 canciones
        Assert.assertEquals(2, songs.size)
        Assert.assertTrue(songs.all { it.artistName == "Common Artist" })
    }

    @Test
    @Throws(Exception::class)
    fun getAllArtistsFlow_emitsUniqueArtists() = runTest {
        // Given: Varias canciones con artistas repetidos
        songArtistDao.insert(SongArtistEntity.create(20L, "Artist X", 0))
        songArtistDao.insert(SongArtistEntity.create(21L, "Artist Y", 0))
        songArtistDao.insert(SongArtistEntity.create(22L, "Artist X", 0)) // Repetido
        
        // When: Obtener todos los artistas únicos
        val allArtists = songArtistDao.getAllArtistsFlow().first()
        
        // Then: Artistas únicos ordenados alfabéticamente
        Assert.assertEquals(2, allArtists.size)
        Assert.assertEquals(listOf("Artist X", "Artist Y"), allArtists)
    }

    @Test
    @Throws(Exception::class)
    fun countSongsForArtist() = runTest {
        // Given: Artista con 5 canciones
        repeat(5) { i ->
            songArtistDao.insert(SongArtistEntity.create(30L + i, "Prolific Artist", 0))
        }
        songArtistDao.insert(SongArtistEntity.create(35L, "Other Artist", 0))
        
        // When: Contar canciones
        val count = songArtistDao.countSongsForArtist("Prolific Artist")
        
        // Then: Debe retornar 5
        Assert.assertEquals(5, count)
    }

    @Test
    @Throws(Exception::class)
    fun deleteAllForSong_cascadesArtists() = runTest {
        // Given: Canción con 4 artistas
        val songId = 40L
        repeat(4) { i ->
            songArtistDao.insert(SongArtistEntity.create(songId, "Artist $i", i))
        }
        
        // When: Eliminar todos los artistas de la canción
        songArtistDao.deleteAllForSong(songId)
        
        // Then: No deben quedar artistas para esa canción
        val remainingArtists = songArtistDao.getArtistsForSong(songId)
        Assert.assertTrue(remainingArtists.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun getArtistNamesForSong_returnsOnlyNames() = runTest {
        // Given: Canción con 3 artistas
        val songId = 50L
        songArtistDao.insert(SongArtistEntity.create(songId, "First Artist", 0))
        songArtistDao.insert(SongArtistEntity.create(songId, "Second Artist", 1))
        songArtistDao.insert(SongArtistEntity.create(songId, "Third Artist", 2))
        
        // When: Obtener solo nombres
        val names = songArtistDao.getArtistNamesForSong(songId)
        
        // Then: Debe retornar lista de nombres en orden
        Assert.assertEquals(3, names.size)
        Assert.assertEquals(listOf("First Artist", "Second Artist", "Third Artist"), names)
    }

    @Test
    @Throws(Exception::class)
    fun insertAll_multipleArtistsAtOnce() = runTest {
        // Given: Lista de artistas
        val songId = 60L
        val artists = listOf(
            SongArtistEntity.create(songId, "Artist 1", 0),
            SongArtistEntity.create(songId, "Artist 2", 1),
            SongArtistEntity.create(songId, "Artist 3", 2)
        )
        
        // When: Insertar todos de una vez
        songArtistDao.insertAll(artists)
        
        // Then: Verificar que todos fueron insertados
        val result = songArtistDao.getArtistsForSong(songId)
        Assert.assertEquals(3, result.size)
        Assert.assertEquals("Artist 1", result[0].artistName)
        Assert.assertEquals("Artist 3", result[2].artistName)
    }

    @Test
    @Throws(Exception::class)
    fun deleteBySongId_removesSongFromAllArtists() = runTest {
        // Given: Múltiples canciones con artistas compartidos
        songArtistDao.insert(SongArtistEntity.create(70L, "Shared Artist", 0))
        songArtistDao.insert(SongArtistEntity.create(71L, "Shared Artist", 0))
        songArtistDao.insert(SongArtistEntity.create(72L, "Shared Artist", 0))
        
        // When: Eliminar canción específica
        songArtistDao.deleteBySongId(71L)
        
        // Then: Solo deben quedar 2 canciones para ese artista
        val songs = songArtistDao.getSongsForArtist("Shared Artist")
        Assert.assertEquals(2, songs.size)
        Assert.assertTrue(songs.none { it.songId == 71L })
    }
}
