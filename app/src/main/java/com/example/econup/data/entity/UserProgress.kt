package com.example.econup.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val wordId: String = "",
    val isLearned: Boolean = false,
    val isBookmarked: Boolean = false,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val lastStudiedAt: Long = 0L
)