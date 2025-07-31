package com.nextbigthing.habitly.room

import android.content.Context
import androidx.room.Room

object RoomHelper {

    fun getDatabase(context: Context): AppDatabase{
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "database-name"
        )
            .addMigrations(MIGRATION_2_3)
            .build()
        return db
    }

}