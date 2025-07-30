package com.nextbigthing.habitly.room.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HabitStatus(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: Int,
    val date: String,
    val isCompleted: Boolean
)
