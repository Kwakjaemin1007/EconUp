package com.example.econup.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.econup.data.entity.DailyStreak

@Dao
interface StreakDao {
    @Query("SELECT * FROM daily_streak WHERE date = :date")
    suspend fun getStreak(date: String): DailyStreak?

    @Upsert
    suspend fun upsertStreak(streak: DailyStreak)
}