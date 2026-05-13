package com.example.econup.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.econup.data.entity.EconomyWord
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM economy_words WHERE category = :category")
    fun getWordsByCategory(category: String): Flow<List<EconomyWord>>

    @Query("SELECT * FROM economy_words WHERE term LIKE '%' || :searchQuery || '%'")
    fun searchWords(searchQuery: String): Flow<List<EconomyWord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<EconomyWord>)

    @Query("SELECT COUNT(*) FROM economy_words")
    suspend fun getWordCount(): Int
}