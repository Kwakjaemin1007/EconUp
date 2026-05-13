package com.example.econup

import android.app.Application
import com.example.econup.data.repository.WordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EconUpApplication : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val wordRepository by lazy {
        WordRepository(this, database.wordDao(), database.progressDao())
    }

    override fun onCreate() {
        super.onCreate()

//        // 앱 시작 시 백그라운드에서 Firestore 동기화 체크
//        CoroutineScope(Dispatchers.IO).launch {
//            wordRepository.syncFromFirestoreIfNeeded()
//        }
    }
}