package com.example.econup.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.econup.data.db.ProgressDao
import com.example.econup.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// UI에 노출할 카테고리 진행도 데이터 클래스
data class CategoryProgressState(
    val name: String,
    val emoji: String,
    val totalWords: Int,
    val learnedWords: Int,
    val progressRate: Float
)

class CategoryViewModel(
    private val wordRepository: WordRepository,
    private val progressDao: ProgressDao // 진행도 단건 조회를 위해 주입
) : ViewModel() {

    private val categories = listOf(
        "주식 & 투자" to "📈",
        "금융 기초" to "🏦",
        "부동산" to "🏠",
        "거시경제" to "🌍",
        "암호화폐" to "🪙"
    )

    private val _categoryProgressList = MutableStateFlow<List<CategoryProgressState>>(emptyList())
    val categoryProgressList: StateFlow<List<CategoryProgressState>> = _categoryProgressList.asStateFlow()

    init {
        loadCategoryProgress()
    }

    private fun loadCategoryProgress() {
        viewModelScope.launch {
            val progressList = categories.map { (name, emoji) ->
                // 카테고리별 전체 단어 가져오기
                val words = wordRepository.getWordsByCategory(name).first()
                val totalWords = words.size

                // 완료(isLearned = true)된 단어 수 계산
                var learnedWords = 0
                for (word in words) {
                    val progress = progressDao.getProgressByWordId(word.id)
                    if (progress?.isLearned == true) {
                        learnedWords++
                    }
                }

                val progressRate = if (totalWords > 0) learnedWords.toFloat() / totalWords.toFloat() else 0f

                CategoryProgressState(
                    name = name,
                    emoji = emoji,
                    totalWords = totalWords,
                    learnedWords = learnedWords,
                    progressRate = progressRate
                )
            }
            _categoryProgressList.value = progressList
        }
    }
}