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

package com.mardous.booming.extensions.media

import androidx.media3.common.Player

/**
 * Data class representing a chapter in media content.
 */
data class ChapterInfo(
    val title: String,
    val startTimeMs: Long,
    val endTimeMs: Long,
    val id: String = ""
)

/**
 * Extracts chapters from the player's current media metadata.
 * Supports both ID3 chapters (CHAP frames) and MP4 chapters.
 *
 * @receiver The Player instance to extract chapters from
 * @return List of ChapterInfo objects, or empty list if no chapters found
 */
fun Player.getChapters(): List<ChapterInfo> {
    val metadata = mediaMetadata.extras
    if (metadata == null) {
        return emptyList()
    }

    val chapters = mutableListOf<ChapterInfo>()

    // Try to get chapter information from extras bundle
    // Media3 stores chapter info in the extras bundle
    val chapterData = metadata.getParcelableArray("chapters")

    if (chapterData != null) {
        chapterData.forEach { chapter ->
            if (chapter is android.os.Bundle) {
                val title = chapter.getString("title") ?: "Unknown"
                val startTimeMs = chapter.getLong("startTimeMs", 0)
                val endTimeMs = chapter.getLong("endTimeMs", 0)
                val id = chapter.getString("id") ?: ""

                chapters.add(ChapterInfo(title, startTimeMs, endTimeMs, id))
            }
        }
    }

    return chapters.sortedBy { it.startTimeMs }
}

/**
 * Checks if the current media has chapters.
 */
fun Player.hasChapters(): Boolean = getChapters().isNotEmpty()

/**
 * Gets the current chapter based on playback position.
 *
 * @return Current ChapterInfo or null if not in a chapter or no chapters exist
 */
fun Player.getCurrentChapter(): ChapterInfo? {
    val chapters = getChapters()
    if (chapters.isEmpty()) {
        return null
    }

    val currentPosition = currentPosition

    return chapters.find { chapter ->
        currentPosition >= chapter.startTimeMs && currentPosition < chapter.endTimeMs
    }
}

/**
 * Gets the next chapter from the current position.
 *
 * @return Next ChapterInfo or null if at the last chapter
 */
fun Player.getNextChapter(): ChapterInfo? {
    val chapters = getChapters()
    if (chapters.isEmpty()) {
        return null
    }

    val currentPosition = currentPosition

    return chapters.find { chapter ->
        currentPosition < chapter.startTimeMs
    }
}

/**
 * Gets the previous chapter from the current position.
 *
 * @return Previous ChapterInfo or null if at the first chapter
 */
fun Player.getPreviousChapter(): ChapterInfo? {
    val chapters = getChapters()
    if (chapters.isEmpty()) {
        return null
    }

    val currentPosition = currentPosition

    // Find the last chapter that ends before current position
    return chapters
        .filter { it.endTimeMs <= currentPosition }
        .lastOrNull()
        ?: chapters.firstOrNull()?.takeIf { currentPosition > it.endTimeMs }
}

/**
 * Seeks to the start of the specified chapter.
 *
 * @param chapter The chapter to seek to
 * @return true if seek was successful, false otherwise
 */
fun Player.seekToChapter(chapter: ChapterInfo): Boolean {
    if (chapter.startTimeMs < 0) {
        return false
    }

    seekTo(chapter.startTimeMs)
    return true
}

/**
 * Seeks to the next chapter.
 *
 * @return true if seek was successful, false if no next chapter exists
 */
fun Player.seekToNextChapter(): Boolean {
    val nextChapter = getNextChapter()
    return nextChapter?.let { seekToChapter(it) } ?: false
}

/**
 * Seeks to the previous chapter.
 *
 * @return true if seek was successful, false if no previous chapter exists
 */
fun Player.seekToPreviousChapter(): Boolean {
    val prevChapter = getPreviousChapter()
    return prevChapter?.let { seekToChapter(it) } ?: false
}
