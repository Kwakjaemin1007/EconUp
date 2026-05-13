package com.example.econup.data.repository

import android.content.Context
import android.util.Log
import com.example.econup.data.db.ProgressDao
import com.example.econup.data.db.WordDao
import com.example.econup.data.entity.EconomyWord
import com.example.econup.data.entity.UserProgress
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Source
// 1. Repository 클래스를 선언하고, 생성자로 실제 Dao 객체들을 받습니다.
class WordRepository(
    private val wordDao: WordDao,         // 설계도가 아닌 실제 'wordDao' 객체
    private val progressDao: ProgressDao  // 설계도가 아닌 실제 'progressDao' 객체
) {

    // Firestore 동기화 로직
    suspend fun syncFromFirestoreIfNeeded() {
        try {
            Log.d("FirestoreSync", "🚨 동기화 함수 실행됨!")
            val wordCount = wordDao.getWordCount()

            if (wordCount == 0) {
                Log.d("FirestoreSync", "👉 DB가 비어있으니 Firestore 접속 시도...")
                val firestore = FirebaseFirestore.getInstance()

                // 🔥 추가된 CCTV 1: 현재 앱이 찾아간 Firebase 프로젝트 이름 확인
                Log.d("FirestoreSync", "🔥 연결된 프로젝트 ID: ${firestore.app.options.projectId}")

                // 🔥 추가된 CCTV 2: 과거 기억(캐시) 버리고 무조건 진짜 서버(SERVER)에서 가져오기!
                val snapshot = firestore.collection("words").get().await()

                Log.d("FirestoreSync", "👉 Firestore에서 가져온 문서 개수: ${snapshot.size()}")

                val remoteWords = snapshot.toObjects(EconomyWord::class.java)
                if (remoteWords.isNotEmpty()) {
                    wordDao.insertAll(remoteWords)
                    Log.d("FirestoreSync", "✅ 로컬 DB에 단어 저장 성공!")
                } else {
                    Log.d("FirestoreSync", "❌ 가져왔는데 빈 데이터이거나 변환 실패")
                }
            } else {
                Log.d("FirestoreSync", "✅ 이미 데이터가 있어서 동기화 건너뜀")
            }
        } catch (e: Exception) {
            Log.e("FirestoreSync", "🔥 동기화 중 에러 펑!!", e)
        }
    }

    // 2. 정답 기록 로직 (2번 맞히면 학습 완료)
    suspend fun recordAnswer(wordId: String, isCorrect: Boolean) {
        // 💡 대문자 ProgressDao가 아니라, 소문자 progressDao를 사용합니다!
        val currentProgress = progressDao.getProgressByWordId(wordId) ?: UserProgress(wordId = wordId)

        val newCorrectCount = if (isCorrect) currentProgress.correctCount + 1 else currentProgress.correctCount
        val newWrongCount = if (!isCorrect) currentProgress.wrongCount + 1 else currentProgress.wrongCount

        val updatedProgress = currentProgress.copy(
            correctCount = newCorrectCount,
            wrongCount = newWrongCount,
            isLearned = newCorrectCount >= 2, // 2번 이상 맞히면 true
            lastStudiedAt = System.currentTimeMillis()
        )
        progressDao.upsertProgress(updatedProgress) // 여기도 소문자!
    }

    // 3. 북마크 토글
    suspend fun toggleBookmark(wordId: String) {
        // 💡 여기도 소문자 progressDao
        val currentProgress = progressDao.getProgressByWordId(wordId) ?: UserProgress(wordId = wordId)
        val updatedProgress = currentProgress.copy(
            isBookmarked = !currentProgress.isBookmarked
        )
        progressDao.upsertProgress(updatedProgress) // 여기도 소문자!
    }
}