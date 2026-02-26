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

package com.mardous.booming.data.local.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

/**
 * Cache para almacenar resultados del scanner de archivos independiente.
 * Esto evita depender exclusivamente de MediaStore para lecturas posteriores.
 * 
 * Nota: Usamos filePath como clave única en vez de MediaStore ID para mayor estabilidad.
 */
@Entity(
    tableName = "scanned_media_cache",
    indices = [
        Index(value = ["filePath"], unique = true),
        Index(value = ["lastModified"]),
        Index(value = ["isValid"])
    ]
)
data class ScannedMediaCache(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "cache_id")
    val cacheId: Long = 0,

    // Identificación única del archivo
    @ColumnInfo(name = "file_path")
    val filePath: String,

    @ColumnInfo(name = "file_name")
    val fileName: String,

    @ColumnInfo(name = "file_size")
    val fileSize: Long,

    @ColumnInfo(name = "last_modified")
    val lastModified: Long,

    // Metadatos de audio (extraídos con TagLib)
    @ColumnInfo(name = "title")
    val title: String?,

    @ColumnInfo(name = "artist")
    val artist: String?,

    @ColumnInfo(name = "album")
    val album: String?,

    @ColumnInfo(name = "album_artist")
    val albumArtist: String?,

    @ColumnInfo(name = "genre")
    val genre: String?,

    @ColumnInfo(name = "year")
    val year: Int?,

    @ColumnInfo(name = "track_number")
    val trackNumber: Int?,

    @ColumnInfo(name = "duration")
    val duration: Long?,

    @ColumnInfo(name = "bitrate")
    val bitrate: Int?,

    @ColumnInfo(name = "sample_rate")
    val sampleRate: Int?,

    // Metadatos del escaneo
    @ColumnInfo(name = "scan_timestamp")
    val scanTimestamp: Long,

    // Para vincular con MediaStore si existe (compatibilidad hacia atrás)
    @ColumnInfo(name = "media_store_id")
    val mediaStoreId: Long?,

    // Estado
    @ColumnInfo(name = "is_valid")
    val isValid: Boolean = true
)
