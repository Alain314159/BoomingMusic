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

package com.mardous.booming.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mardous.booming.data.local.room.entity.SongArtistEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para gestionar la relación muchos-a-muchos entre canciones y artistas
 */
@Dao
interface SongArtistDao {

    /**
     * Obtiene todos los artistas de una canción como Flow
     */
    @Query("SELECT * FROM song_artist WHERE song_id = :songId ORDER BY artist_order ASC")
    fun getArtistsForSongFlow(songId: Long): Flow<List<SongArtistEntity>>

    /**
     * Obtiene todos los artistas de una canción (suspend)
     */
    @Query("SELECT * FROM song_artist WHERE song_id = :songId ORDER BY artist_order ASC")
    suspend fun getArtistsForSong(songId: Long): List<SongArtistEntity>

    /**
     * Obtiene los nombres de los artistas para una canción
     */
    @Query("SELECT artist_name FROM song_artist WHERE song_id = :songId ORDER BY artist_order ASC")
    suspend fun getArtistNamesForSong(songId: Long): List<String>

    /**
     * Obtiene todas las canciones de un artista
     */
    @Query("SELECT * FROM song_artist WHERE artist_name = :artistName")
    suspend fun getSongsForArtist(artistName: String): List<SongArtistEntity>

    /**
     * Agrega un artista a una canción
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(songArtist: SongArtistEntity)

    /**
     * Agrega múltiples artistas a una canción
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(songArtists: List<SongArtistEntity>)

    /**
     * Elimina un artista específico de una canción
     */
    @Delete
    suspend fun delete(songArtist: SongArtistEntity)

    /**
     * Elimina todos los artistas de una canción
     */
    @Query("DELETE FROM song_artist WHERE song_id = :songId")
    suspend fun deleteAllForSong(songId: Long)

    /**
     * Elimina una canción de todos los artistas (limpieza)
     */
    @Query("DELETE FROM song_artist WHERE song_id = :songId")
    suspend fun deleteBySongId(songId: Long)

    /**
     * Actualiza el orden de los artistas para una canción
     */
    @Query("UPDATE song_artist SET artist_order = :order WHERE song_id = :songId AND artist_name = :artistName")
    suspend fun updateArtistOrder(songId: Long, artistName: String, order: Int)

    /**
     * Obtiene todos los artistas únicos ordenados alfabéticamente
     */
    @Query("SELECT DISTINCT artist_name FROM song_artist ORDER BY artist_name ASC")
    fun getAllArtistsFlow(): Flow<List<String>>

    /**
     * Cuenta cuántas canciones tiene un artista
     */
    @Query("SELECT COUNT(*) FROM song_artist WHERE artist_name = :artistName")
    suspend fun countSongsForArtist(artistName: String): Int
}
