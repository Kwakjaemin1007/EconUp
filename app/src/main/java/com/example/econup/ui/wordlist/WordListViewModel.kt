package com.example.econup.ui.wordlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.econup.data.entity.EconomyWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class WordListViewModel : ViewModel() {
    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow("전체")

    // 더미 데이터 (Repository 연결 전 UI 테스트용)
    private val allWords = MutableStateFlow(
        listOf(
            EconomyWord("1", "GDP", "Gross Domestic Product", "국내총생산. 한 나라 안에서 생산된 모든 재화와 서비스의 시장가치.", "한국의 GDP가 상승했다.", "거시경제", 2),
            EconomyWord("2", "인플레이션", "Inflation", "물가가 지속적으로 오르는 현상.", "인플레이션으로 인해 밥값이 올랐다.", "거시경제", 1)
        )
    )

    // combine을 이용한 필터링 로직
    val filteredWords: StateFlow<List<EconomyWord>> = combine(
        searchQuery, selectedFilter, allWords
    ) { query, filter, words ->
        words.filter { word ->
            (query.isEmpty() || word.term.contains(query, ignoreCase = true)) &&
                    (filter == "전체" || word.category == filter) // 나중에 북마크/학습완료 조건 추가
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun updateFilter(filter: String) {
        selectedFilter.value = filter
    }
}