package com.example.econup.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.econup.data.db.ProgressDao
import com.example.econup.data.repository.WordRepository
import kotlinx.coroutines.flow.*

// 화면에 보여줄 데이터 묶음
data class UserProfileState(
    val totalWords: Int = 0,
    val learnedWords: Int = 0,
    val accuracy: Int = 0,
    val badges: List<Pair<String, String>> = emptyList() // "이모지" to "배지이름"
)

class ProfileViewModel(
    private val wordRepository: WordRepository,
    private val progressDao: ProgressDao
) : ViewModel() {

    private val allWords = wordRepository.getAllWords()
    private val allProgress = progressDao.getAllProgress()

    // 단어와 진행도를 실시간으로 감시해서 통계를 냅니다.
    val uiState: StateFlow<UserProfileState> = combine(allWords, allProgress) { words, progresses ->
        val totalWords = words.size
        val learnedWords = progresses.count { it.isLearned }

        // 정답률 계산
        var totalCorrect = 0
        var totalWrong = 0
        progresses.forEach {
            totalCorrect += it.correctCount
            totalWrong += it.wrongCount
        }
        val accuracy = if (totalCorrect + totalWrong > 0) {
            ((totalCorrect.toFloat() / (totalCorrect + totalWrong).toFloat()) * 100).toInt()
        } else {
            0
        }

        // 조건에 따라 배지 획득 로직
        val earnedBadges = mutableListOf<Pair<String, String>>()
        earnedBadges.add("🐣" to "경제 뉴비") // 기본 배지
        if (learnedWords >= 1) earnedBadges.add("🌱" to "첫 걸음")
        if (learnedWords >= 10) earnedBadges.add("🔥" to "열공러")
        if (totalCorrect >= 50) earnedBadges.add("🧠" to "경제 브레인")
        if (accuracy >= 80 && totalCorrect > 10) earnedBadges.add("🎯" to "백발백중")

        UserProfileState(
            totalWords = totalWords,
            learnedWords = learnedWords,
            accuracy = accuracy,
            badges = earnedBadges
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserProfileState()
    )
}