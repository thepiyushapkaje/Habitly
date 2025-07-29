package com.nextbigthing.habitly.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SaveDate(
    @PrimaryKey(true) val uid:Int,
    @ColumnInfo("date") val date: String
)
