package com.nextbigthing.habitly.room.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Habit(
    @PrimaryKey(autoGenerate = true) val uid: Int=0,
    @ColumnInfo(name = "title") var firstName: String?,
    @ColumnInfo(name = "status") var isCompleted: Boolean?
)