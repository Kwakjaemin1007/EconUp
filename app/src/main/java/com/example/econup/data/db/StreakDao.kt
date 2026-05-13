package com.example.econup.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.econup.data.entity.DailyStreak
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    @Query("SELECT * FROM daily_streak WHERE date = :date")
    fun getStreakByDate(date: String): Flow<DailyStreak?>

    @Upsert
    suspend fun upsertStreak(streak: DailyStreak)
}