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

package com.mardous.booming.data.scanner

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.mardous.booming.data.local.MetadataReader
import com.mardous.booming.util.FileUtil
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

/**
 * Información de un archivo de audio escaneado.
 */
data class AudioFileInfo(
    val uri: Uri,
    val filePath: String,
    val fileName: String,
    val size: Long,
    val lastModified: Long,
    val title: String?,
    val artist: String?,
    val album: String?,
    val albumArtist: String?,
    val genre: String?,
    val year: Int?,
    val trackNumber: Int?,
    val duration: Long?,
    val bitrate: Int?,
    val sampleRate: Int?
)

/**
 * Scanner de archivos de audio que soporta tanto acceso directo como SAF.
 * Usa MetadataReader existente para extraer tags.
 */
class FileScanner : KoinComponent {

    private val context: Context by inject()

    companion object {
        private val AUDIO_EXTENSIONS = setOf(
            "mp3", "wav", "ogg", "m4a", "flac", "aac", "wma", "opus", "ape"
        )
        private const val TAG = "FileScanner"
    }

    /**
     * Escanea un directorio y retorna lista de archivos de audio.
     * Soporta tanto acceso directo como SAF (Storage Access Framework).
     *
     * @param directory Directorio a escanear (puede ser File normal o DocumentFile)
     * @param onProgress Callback para progreso (current, total, fileName)
     */
    suspend fun scanDirectory(
        directory: File,
        onProgress: (current: Int, total: Int, fileName: String) -> Unit = { _, _, _ -> }
    ): List<AudioFileInfo> {
        val audioFiles = mutableListOf<AudioFileInfo>()

        try {
            // Escaneo recursivo con walk()
            val allFiles = directory.walkTopDown()
                .filter { it.isFile && it.extension.lowercase() in AUDIO_EXTENSIONS }
                .toList()

            val total = allFiles.size
            allFiles.forEachIndexed { index, file ->
                val fileName = file.name
                onProgress(index + 1, total, fileName)

                try {
                    val info = createAudioFileInfo(file)
                    if (info != null) {
                        audioFiles.add(info)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to process: ${file.absolutePath}", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error scanning directory: ${directory.absolutePath}", e)
        }

        return audioFiles
    }

    /**
     * Escanea un directorio usando SAF (Storage Access Framework).
     * Para carpetas seleccionadas por el usuario en Android 10+.
     *
     * @param treeUri URI del árbol de documentos
     * @param onProgress Callback para progreso
     */
    suspend fun scanDocumentDirectory(
        treeUri: Uri,
        onProgress: (current: Int, total: Int, fileName: String) -> Unit = { _, _, _ -> }
    ): List<AudioFileInfo> {
        val audioFiles = mutableListOf<AudioFileInfo>()

        try {
            val documentFile = DocumentFile.fromTreeUri(context, treeUri) ?: return audioFiles
            scanDocumentFileRecursive(documentFile, audioFiles, onProgress)
        } catch (e: Exception) {
            Log.e(TAG, "Error scanning SAF directory: $treeUri", e)
        }

        return audioFiles
    }

    /**
     * Escaneo recursivo de DocumentFile.
     */
    private fun scanDocumentFileRecursive(
        directory: DocumentFile,
        audioFiles: MutableList<AudioFileInfo>,
        onProgress: (Int, Int, String) -> Unit,
        currentCount: Int = 0
    ): Int {
        var count = currentCount
        val files = directory.listFiles()

        // Primera pasada: contar archivos
        val audioDocuments = files.filter { doc ->
            doc.isFile && doc.name?.let {
                it.substringAfterLast('.', "").lowercase() in AUDIO_EXTENSIONS
            } == true
        }

        val total = audioDocuments.size

        // Segunda pasada: procesar archivos
        audioDocuments.forEach { doc ->
            val fileName = doc.name ?: "Unknown"
            count++
            onProgress(count, total, fileName)

            try {
                doc.uri.let { uri ->
                    val info = createAudioFileInfoFromUri(uri, doc.name ?: "Unknown", doc.length(), doc.lastModified())
                    if (info != null) {
                        audioFiles.add(info)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to process: ${doc.uri}", e)
            }
        }

        // Recursión a subdirectorios
        files.filter { it.isDirectory }.forEach { subDir ->
            count = scanDocumentFileRecursive(subDir, audioFiles, onProgress, count)
        }

        return count
    }

    /**
     * Crea AudioFileInfo desde un File regular.
     */
    private fun createAudioFileInfo(file: File): AudioFileInfo? {
        val uri = Uri.fromFile(file)
        return createAudioFileInfoFromUri(uri, file.absolutePath, file.length(), file.lastModified())
    }

    /**
     * Crea AudioFileInfo desde URI extrayendo metadatos con MetadataReader.
     */
    private fun createAudioFileInfoFromUri(
        uri: Uri,
        filePath: String,
        fileSize: Long,
        fileLastModified: Long
    ): AudioFileInfo? {
        return try {
            var title: String? = null
            var artist: String? = null
            var album: String? = null
            var albumArtist: String? = null
            var genre: String? = null
            var year: Int? = null
            var trackNumber: Int? = null
            var duration: Long? = null
            var bitrate: Int? = null
            var sampleRate: Int? = null

            // Usar MetadataReader existente para tags
            val reader = MetadataReader(uri, readPictures = false)
            if (reader.hasMetadata) {
                title = reader.first(MetadataReader.TITLE)
                artist = reader.first(MetadataReader.ARTIST)
                album = reader.first(MetadataReader.ALBUM)
                albumArtist = reader.first(MetadataReader.ALBUM_ARTIST)
                genre = reader.genre()

                val yearStr = reader.first(MetadataReader.YEAR)
                year = yearStr?.take(4)?.toIntOrNull()

                val trackStr = reader.first(MetadataReader.TRACK_NUMBER)
                trackNumber = trackStr?.toIntOrNull()

                // Bitrate y sample rate de audio properties
                // MetadataReader los tiene pero no los expone directamente
            }

            // Obtener duración desde FileUtil existente (fallback cuando MediaStore falla)
            duration = FileUtil.getDurationFromTag(uri).takeIf { it > 0 }

            AudioFileInfo(
                uri = uri,
                filePath = filePath,
                fileName = Uri.parse(filePath).lastPathSegment ?: "Unknown",
                size = fileSize,
                lastModified = fileLastModified,
                title = title,
                artist = artist,
                album = album,
                albumArtist = albumArtist,
                genre = genre,
                year = year,
                trackNumber = trackNumber,
                duration = duration,
                bitrate = bitrate,
                sampleRate = sampleRate
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error creating AudioFileInfo for $uri", e)
            null
        }
    }
}
