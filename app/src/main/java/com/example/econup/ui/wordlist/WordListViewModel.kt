package com.example.econup.ui.wordlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.econup.data.db.ProgressDao
import com.example.econup.data.entity.EconomyWord
import com.example.econup.data.repository.WordRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// 단어 정보와 사용자의 학습/북마크 상태를 하나로 묶어 UI로 보낼 데이터 클래스
data class WordUiModel(
    val word: EconomyWord,
    val isBookmarked: Boolean,
    val isLearned: Boolean
)

class WordListViewModel(
    private val wordRepository: WordRepository,
    private val progressDao: ProgressDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow("전체")

    // DB에서 전체 단어와 전체 진행도를 가져옴
    private val allWords = wordRepository.getAllWords()
    private val allProgress = progressDao.getAllProgress()

    // 1. 단어 + 진행도 결합
    private val wordUiModels: Flow<List<WordUiModel>> = combine(allWords, allProgress) { words, progresses ->
        words.map { word ->
            val progress = progresses.find { it.wordId == word.id }
            WordUiModel(
                word = word,
                isBookmarked = progress?.isBookmarked == true,
                isLearned = progress?.isLearned == true
            )
        }
    }

    // 2. 검색어 및 필터 적용
    val filteredWords: StateFlow<List<WordUiModel>> = combine(
        searchQuery, selectedFilter, wordUiModels
    ) { query, filter, models ->
        models.filter { model ->
            // 검색어 포함 여부
            val matchesQuery = query.isEmpty() || model.word.term.contains(query, ignoreCase = true)

            // 필터 칩 조건
            val matchesFilter = when (filter) {
                "전체" -> true
                "학습완료" -> model.isLearned
                "북마크" -> model.isBookmarked
                else -> model.word.category == filter // 예: "거시경제"
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) { searchQuery.value = query }
    fun updateFilter(filter: String) { selectedFilter.value = filter }

    // 북마크 클릭 시 Repository를 통해 DB 업데이트
    fun toggleBookmark(wordId: String) {
        viewModelScope.launch {
            wordRepository.toggleBookmark(wordId)
        }
    }
}