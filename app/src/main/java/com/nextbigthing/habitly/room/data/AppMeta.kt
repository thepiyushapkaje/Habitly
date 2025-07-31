package com.nextbigthing.habitly.room.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppMeta(
    @PrimaryKey val id: Int = 1,
    val lastResetDate: String
)