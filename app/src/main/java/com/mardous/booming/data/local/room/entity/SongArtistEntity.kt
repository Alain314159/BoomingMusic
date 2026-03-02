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

package com.mardous.booming.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad para la relación muchos-a-muchos entre canciones y artistas
 *
 * Una canción puede tener múltiples artistas (colaboraciones, features, etc.)
 * Un artista puede tener múltiples canciones
 */
@Entity(
    tableName = "song_artist",
    foreignKeys = [
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["song_key"],
            childColumns = ["song_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["song_id"]),
        Index(value = ["artist_name"]),
        Index(value = ["song_id", "artist_name"], unique = true)
    ]
)
data class SongArtistEntity(
    /**
     * ID de la canción (referencia a SongEntity.songPrimaryKey)
     */
    @ColumnInfo(name = "song_id")
    val songId: Long,

    /**
     * Nombre del artista (puede repetirse en múltiples canciones)
     * No usamos ID separado para simplificar - el nombre es la clave
     */
    @ColumnInfo(name = "artist_name")
    val artistName: String,

    /**
     * Orden del artista en la lista (para mantener el orden de features)
     * Ej: "Artista Principal" = 0, "Feature" = 1, etc.
     */
    @ColumnInfo(name = "artist_order")
    val artistOrder: Int = 0
) {
    companion object {
        /**
         * Crea una relación canción-artista
         */
        fun create(
            songId: Long,
            artistName: String,
            artistOrder: Int = 0
        ): SongArtistEntity {
            return SongArtistEntity(
                songId = songId,
                artistName = artistName,
                artistOrder = artistOrder
            )
        }
    }
}
