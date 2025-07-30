package com.nextbigthing.habitly.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nextbigthing.habitly.room.data.Habit
import com.nextbigthing.habitly.room.data.HabitStatus

@Database(entities = [Habit::class, HabitStatus::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}