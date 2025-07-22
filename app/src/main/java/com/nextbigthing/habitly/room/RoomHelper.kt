package com.nextbigthing.habitly.room

import android.content.Context
import androidx.room.Room

object RoomHelper {

    fun getDatabase(context: Context): AppDatabase{
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()
        return db
    }

}