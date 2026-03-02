package com.mardous.booming.data.model

import kotlinx.parcelize.Parcelize

@Parcelize
class ExpandedSong(
    override val id: Long,
    override val data: String,
    override val title: String,
    override val trackNumber: Int,
    override val year: Int,
    override val size: Long,
    override val duration: Long,
    override val dateAdded: Long,
    override val rawDateModified: Long,
    override val albumId: Long,
    override val albumName: String,
    override val artistId: Long,
    override val artistName: String,
    override val albumArtistName: String?,
    override val genreName: String?,
    val playCount: Int,
    val skipCount: Int,
    val lastPlayedAt: Long,
    val isFavorite: Boolean,
    override val artists: List<String> = emptyList()  // Multi-artist support
) : Song(
    id,
    data,
    title,
    trackNumber,
    year,
    size,
    duration,
    dateAdded,
    rawDateModified,
    albumId,
    albumName,
    artistId,
    artistName,
    albumArtistName,
    genreName,
    artists = artists.takeIf { it.isNotEmpty() } ?: listOfNotNull(artistName.takeUnless { it.isBlank() })
) {

    constructor(
        song: Song,
        playCount: Int,
        skipCount: Int,
        lastPlayedAt: Long,
        isFavorite: Boolean
    ) : this(
        id = song.id,
        data = song.data,
        title = song.title,
        trackNumber = song.trackNumber,
        year = song.year,
        size = song.size,
        duration = song.duration,
        dateAdded = song.dateAdded,
        rawDateModified = song.rawDateModified,
        albumId = song.albumId,
        albumName = song.albumName,
        artistId = song.artistId,
        artistName = song.artistName,
        albumArtistName = song.albumArtistName,
        genreName = song.genreName,
        playCount = playCount,
        skipCount = skipCount,
        lastPlayedAt = lastPlayedAt,
        isFavorite = isFavorite,
        artists = song.artists  // Preserve multi-artist from Song
    )
}