package com.example.econup.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_streak")
data class DailyStreak(
    @PrimaryKey val date: String = "", // "YYYY-MM-DD" 포맷
    val studyCount: Int = 0
)