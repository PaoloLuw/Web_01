package com.luwliette.ztmelody_02.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class FavoriteSong(
    val id: Long,
    val title: String,
    val artist: String,
    val data: String,
    val dateAdded: Long
)

class FavoriteSongsDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_FAVORITE_SONGS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY," +
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_ARTIST TEXT," +
                "$COLUMN_DATA TEXT," +
                "$COLUMN_DATE_ADDED INTEGER)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITE_SONGS")
        onCreate(db)
    }

    fun addFavoriteSong(song: FavoriteSong) {
        val db = writableDatabase
        val insertSong = "INSERT INTO $TABLE_FAVORITE_SONGS ($COLUMN_ID, $COLUMN_TITLE, $COLUMN_ARTIST, $COLUMN_DATA, $COLUMN_DATE_ADDED) VALUES (?, ?, ?, ?, ?)"
        val statement = db.compileStatement(insertSong)
        statement.bindLong(1, song.id)
        statement.bindString(2, song.title)
        statement.bindString(3, song.artist)
        statement.bindString(4, song.data)
        statement.bindLong(5, song.dateAdded)
        statement.executeInsert()
        db.close()
    }

    fun removeFavoriteSong(songId: Long) {
        val db = writableDatabase
        db.delete(TABLE_FAVORITE_SONGS, "$COLUMN_ID = ?", arrayOf(songId.toString()))
        db.close()
    }

    fun isFavorite(songId: Long): Boolean {
        val db = readableDatabase
        val cursor = db.query(TABLE_FAVORITE_SONGS, arrayOf(COLUMN_ID), "$COLUMN_ID = ?", arrayOf(songId.toString()), null, null, null)
        val isFavorite = cursor.moveToFirst()
        cursor.close()
        db.close()
        return isFavorite
    }

    companion object {
        private const val DATABASE_NAME = "favorite_songs.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_FAVORITE_SONGS = "favorite_songs"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_ARTIST = "artist"
        const val COLUMN_DATA = "data"
        const val COLUMN_DATE_ADDED = "date_added"
    }
    fun getAllFavoriteSongs(): List<FavoriteSong> {
        val favoriteSongsList = mutableListOf<FavoriteSong>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_FAVORITE_SONGS", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val artist = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST))
                val data = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATA))
                val dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE_ADDED))
                val favoriteSong = FavoriteSong(id, title, artist, data, dateAdded)
                favoriteSongsList.add(favoriteSong)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return favoriteSongsList
    }

}
