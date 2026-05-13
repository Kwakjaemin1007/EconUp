package com.example.econup.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.econup.data.entity.DailyStreak
import com.example.econup.data.entity.EconomyWord
import com.example.econup.data.entity.UserProgress

@Database(
    entities = [EconomyWord::class, UserProgress::class, DailyStreak::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun progressDao(): ProgressDao
    abstract fun streakDao(): StreakDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase { // 함수명 getInstance로 통일
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "econup_database"
                )
                    .fallbackToDestructiveMigration() // 스키마 변경 시 기존 데이터 삭제 후 재생성
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}