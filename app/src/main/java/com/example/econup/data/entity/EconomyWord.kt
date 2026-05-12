package com.example.econup.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "economy_words")
data class EconomyWord(
    @PrimaryKey val id: String = "",
    val term: String = "",
    val fullName: String = "",
    val definition: String = "",
    val example: String = "",
    val category: String = "",
    val difficulty: Int = 1
)