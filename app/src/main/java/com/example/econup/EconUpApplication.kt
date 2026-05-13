package com.example.econup

import android.app.Application
import com.example.econup.data.db.AppDatabase
import com.example.econup.data.repository.WordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EconUpApplication : Application() {

    // 1. 데이터베이스 싱글톤 객체 초기화
    val database by lazy { AppDatabase.getInstance(this) }

    // 2. 🔥 에러가 났던 부분 해결!
    // 이제 쓸데없는 건 빼고, 딱 필요한 2개의 Dao(wordDao, progressDao)만 정확히 넘겨줍니다.
    val wordRepository by lazy {
        WordRepository(
            wordDao = database.wordDao(),
            progressDao = database.progressDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(this)
        val wordRepository = WordRepository(database.wordDao(), database.progressDao())
        // 3. 앱이 켜질 때 Firestore 동기화 실행 (CCTV 로그가 찍힐 겁니다!)
        CoroutineScope(Dispatchers.IO).launch {
            wordRepository.syncFromFirestoreIfNeeded()
        }
    }
}