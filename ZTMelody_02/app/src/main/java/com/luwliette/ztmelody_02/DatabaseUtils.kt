package com.luwliette.ztmelody_02.database

import android.content.Context
import android.database.Cursor

fun searchIdByPath(context: Context, path: String): Long? {
    val dbHelper = SongDatabase(context)
    val db = dbHelper.readableDatabase
    val selectQuery = "SELECT * FROM ${SongDatabase.TABLE_SONGS} WHERE ${SongDatabase.COLUMN_DATA} = ?"
    val cursor: Cursor = db.rawQuery(selectQuery, arrayOf(path))

    var songId: Long? = null
    if (cursor.moveToFirst()) {
        songId = cursor.getLong(cursor.getColumnIndexOrThrow(SongDatabase.COLUMN_ID))
    }
    cursor.close()
    db.close()
    return songId
}
