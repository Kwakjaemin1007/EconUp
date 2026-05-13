package com.example.econup.data.repository

import ProgressDao
import WordDao
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.econup.data.entity.EconomyWord
import com.example.econup.data.entity.UserProgress
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// DataStore 설정을 위한 상단 선언
private val Context.dataStore by preferencesDataStore(name = "settings")

class WordRepository(
    private val context: Context,
    private val wordDao: WordDao,
    private val progressDao: ProgressDao
) {
    private val LAST_SYNCED_KEY = stringPreferencesKey("last_synced")

    // 1. Firestore 동기화 로직
    suspend fun syncFromFirestoreIfNeeded() {
        val wordCount = wordDao.getWordCount()
        // DB가 비어있을 때만 Firestore에서 가져옴 (초기 세팅)
        if (wordCount == 0) {
            try {
                val firestore = FirebaseFirestore.getInstance()
                val snapshot = firestore.collection("words").get().await()
                val remoteWords = snapshot.toObjects(EconomyWord::class.java)

                if (remoteWords.isNotEmpty()) {
                    wordDao.insertAll(remoteWords)
                    // 마지막 동기화 시간 저장
                    context.dataStore.edit { settings ->
                        settings[LAST_SYNCED_KEY] = System.currentTimeMillis().toString()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 2. 정답 기록 로직 (2번 맞히면 학습 완료)
    suspend fun recordAnswer(wordId: String, isCorrect: Boolean) {
        val currentProgress = progressDao.getProgressByWordId(wordId) ?: UserProgress(wordId = wordId)

        val newCorrectCount = if (isCorrect) currentProgress.correctCount + 1 else currentProgress.correctCount
        val newWrongCount = if (!isCorrect) currentProgress.wrongCount + 1 else currentProgress.wrongCount

        val updatedProgress = currentProgress.copy(
            correctCount = newCorrectCount,
            wrongCount = newWrongCount,
            isLearned = newCorrectCount >= 2, // 2번 이상 맞히면 true
            lastStudiedAt = System.currentTimeMillis()
        )
        progressDao.upsertProgress(updatedProgress)
    }

    // 3. 북마크 토글
    suspend fun toggleBookmark(wordId: String) {
        val currentProgress = progressDao.getProgressByWordId(wordId) ?: UserProgress(wordId = wordId)
        val updatedProgress = currentProgress.copy(
            isBookmarked = !currentProgress.isBookmarked
        )
        progressDao.upsertProgress(updatedProgress)
    }
}