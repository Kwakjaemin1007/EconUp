package com.example.econup.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.econup.data.entity.UserProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM user_progress WHERE wordId = :wordId")
    suspend fun getProgressByWordId(wordId: String): UserProgress?

    @Query("SELECT * FROM user_progress WHERE isBookmarked = 1")
    fun getBookmarkedProgress(): Flow<List<UserProgress>>

    @Upsert
    suspend fun upsertProgress(progress: UserProgress)

    @Query("SELECT * FROM user_progress")
    fun getAllProgress(): Flow<List<UserProgress>>
}