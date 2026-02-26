package com.mardous.booming.core

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mardous.booming.data.local.room.*

@Database(
    entities = [
        PlaylistEntity::class,
        SongEntity::class,
        HistoryEntity::class,
        PlayCountEntity::class,
        QueueEntity::class,
        InclExclEntity::class,
        LyricsEntity::class,
        CanvasEntity::class,
        ScannedMediaCache::class  // Nueva entidad para el scanner independiente
    ],
    version = 5,  // Incrementado de 4 a 5
    exportSchema = false
)
abstract class BoomingDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun playCountDao(): PlayCountDao
    abstract fun historyDao(): HistoryDao
    abstract fun queueDao(): QueueDao
    abstract fun inclExclDao(): InclExclDao
    abstract fun lyricsDao(): LyricsDao
    abstract fun canvasDao(): CanvasDao
    abstract fun scannedMediaCacheDao(): ScannedMediaCacheDao  // Nuevo DAO

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE PlaylistEntity ADD COLUMN custom_cover_uri TEXT")
                db.execSQL("ALTER TABLE PlaylistEntity ADD COLUMN description TEXT")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `QueueEntity` (`id` TEXT NOT NULL, `order` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `CanvasEntity` (`id` INT NOT NULL, `canvas_url` TEXT NOT NULL, `fetch_time` INT NOT NULL, PRIMARY KEY(`id`))")
            }
        }

        // Nueva migración para agregar ScannedMediaCache (versión 4 -> 5)
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `scanned_media_cache` (
                        `cache_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `file_path` TEXT NOT NULL,
                        `file_name` TEXT NOT NULL,
                        `file_size` INTEGER NOT NULL,
                        `last_modified` INTEGER NOT NULL,
                        `title` TEXT,
                        `artist` TEXT,
                        `album` TEXT,
                        `album_artist` TEXT,
                        `genre` TEXT,
                        `year` INTEGER,
                        `track_number` INTEGER,
                        `duration` INTEGER,
                        `bitrate` INTEGER,
                        `sample_rate` INTEGER,
                        `scan_timestamp` INTEGER NOT NULL,
                        `media_store_id` INTEGER,
                        `is_valid` INTEGER NOT NULL DEFAULT 1
                    )
                """)
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_scanned_media_cache_filePath` ON `scanned_media_cache` (`file_path`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_scanned_media_cache_lastModified` ON `scanned_media_cache` (`last_modified`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_scanned_media_cache_isValid` ON `scanned_media_cache` (`is_valid`)")
            }
        }
    }
}