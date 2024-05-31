package com.luwliette.ztmelody_02.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val data: String,
    val dateAdded: Long
)

class SongDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_SONGS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY," +
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_ARTIST TEXT," +
                "$COLUMN_DATA TEXT," +
                "$COLUMN_DATE_ADDED INTEGER)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SONGS")
        onCreate(db)
    }

    fun addSong(song: Song) {
        val db = writableDatabase
        val insertSong = "INSERT INTO $TABLE_SONGS ($COLUMN_ID, $COLUMN_TITLE, $COLUMN_ARTIST, $COLUMN_DATA, $COLUMN_DATE_ADDED) VALUES (?, ?, ?, ?, ?)"
        val statement = db.compileStatement(insertSong)
        statement.bindLong(1, song.id)
        statement.bindString(2, song.title)
        statement.bindString(3, song.artist)
        statement.bindString(4, song.data)
        statement.bindLong(5, song.dateAdded)
        statement.executeInsert()
        db.close()
    }
    fun clearAllSongs() {
        val db = this.writableDatabase
        db.delete(TABLE_SONGS, null, null)
        db.close()
    }

    fun getAllSongs(): List<Song> {
        val songList = mutableListOf<Song>()
        val db = readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_SONGS"
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val artist = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST))
                val data = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATA))
                val dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE_ADDED))
                val song = Song(id, title, artist, data, dateAdded)
                songList.add(song)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return songList
    }

    companion object {
        private const val DATABASE_NAME = "songs.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_SONGS = "songs"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_ARTIST = "artist"
        const val COLUMN_DATA = "data"
        const val COLUMN_DATE_ADDED = "date_added"
    }


    fun getAllArtists(): List<String> {
        val artistList = mutableListOf<String>()
        val db = readableDatabase
        val selectQuery = "SELECT DISTINCT $COLUMN_ARTIST FROM $TABLE_SONGS"
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val artist = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST))
                artistList.add(artist)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return artistList
    }

    fun getSongsByArtist(artist: String): List<Song> {
        val songList = mutableListOf<Song>()
        val db = readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_SONGS WHERE $COLUMN_ARTIST = ?"
        val cursor: Cursor = db.rawQuery(selectQuery, arrayOf(artist))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val artist = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST))
                val data = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATA))
                val dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE_ADDED))
                val song = Song(id, title, artist, data, dateAdded)
                songList.add(song)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return songList
    }

}
