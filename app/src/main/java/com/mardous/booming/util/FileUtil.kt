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

package com.mardous.booming.util

import android.content.ContentResolver
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.kyant.taglib.TagLib
import com.mardous.booming.appContext
import org.koin.core.component.KoinComponent
import java.io.File

object FileUtil : KoinComponent {

    // Publicly accessible directories
    const val BOOMING_ARTWORK_DIRECTORY_NAME = "Booming Artwork"
    const val PLAYLISTS_DIRECTORY_NAME = "Playlists"

    // Directories that are accessible only for Booming
    private const val CUSTOM_ARTIST_IMAGES_DIRECTORY_NAME = "custom_artist_images"
    private const val CUSTOM_PLAYLIST_IMAGES_DIRECTORY_NAME = "custom_playlist_images"
    private const val THUMBS_DIRECTORY_NAME = "Thumbs"

    fun externalStorageDirectory(dirType: String? = null): File {
        return if (dirType == null) {
            Environment.getExternalStorageDirectory()
        } else {
            Environment.getExternalStoragePublicDirectory(dirType)
        }
    }

    fun imagesDirectory(dirName: String) =
        externalStorageDirectory(Environment.DIRECTORY_PICTURES).resolve(dirName).ensureDirectory()

    fun playlistsDirectory() =
        externalStorageDirectory().resolve(PLAYLISTS_DIRECTORY_NAME).ensureDirectory()

    fun customArtistImagesDirectory() =
        appContext().filesDir.resolve(CUSTOM_ARTIST_IMAGES_DIRECTORY_NAME).ensureDirectory()

    fun customPlaylistImagesDirectory() =
        appContext().filesDir.resolve(CUSTOM_PLAYLIST_IMAGES_DIRECTORY_NAME).ensureDirectory()

    fun thumbsDirectory() =
        appContext().externalCacheDir?.resolve(THUMBS_DIRECTORY_NAME).ensureDirectory()

    fun getDefaultStartDirectory(): File {
        val musicDir = externalStorageDirectory(Environment.DIRECTORY_MUSIC)
        return if (musicDir.exists() && musicDir.isDirectory) {
            musicDir
        } else {
            val externalStorage = externalStorageDirectory()
            if (externalStorage.exists() && externalStorage.isDirectory) {
                externalStorage
            } else {
                File("/") // root
            }
        }
    }

    /**
     * Gets the duration of an audio file using TagLib as a fallback when MediaStore returns 0.
     * This is particularly useful for M4A files that may report 0 duration in MediaStore.
     *
     * @param uri The URI of the audio file
     * @return Duration in milliseconds, or 0 if unable to read
     */
    fun getDurationFromTag(uri: Uri): Long {
        return try {
            val contentResolver: ContentResolver = appContext().contentResolver
            contentResolver.openFileDescriptor(uri, "r")?.use { fd ->
                val audioProperties = TagLib.getAudioProperties(fd.dup().detachFd())
                audioProperties?.duration?.toLong() ?: 0L
            } ?: 0L
        } catch (t: Throwable) {
            Log.e("DurationReader", "Failed to read duration from tag for $uri", t)
            0L
        }
    }

    private fun File?.ensureDirectory() = takeIf { it != null && (it.exists() || it.mkdirs()) }
}