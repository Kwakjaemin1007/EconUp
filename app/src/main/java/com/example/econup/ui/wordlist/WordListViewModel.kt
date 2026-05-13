package com.example.econup.ui.wordlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.econup.EconUpApplication
import com.example.econup.data.entity.EconomyWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// 일반 ViewModel 대신 Application을 받을 수 있는 AndroidViewModel을 사용합니다.
class WordListViewModel(application: Application) : AndroidViewModel(application) {

    // EconUpApplication에 만들어둔 진짜 DB(wordDao)를 가져옴
    private val wordDao = (application as EconUpApplication).database.wordDao()

    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow("전체")
    // 대신 DB에서 검색어("")를 넣어 모든 단어를 실시간(Flow)으로 가져옴
    private val allWordsFromDB = wordDao.searchWords("")

    // DB에서 가져온 진짜 단어들을 검색어와 필터에 맞게 가공해서 화면(Screen)으로 보냄
    val filteredWords: StateFlow<List<EconomyWord>> = combine(
        searchQuery, selectedFilter, allWordsFromDB
    ) { query, filter, words ->
        words.filter { word ->
            (query.isEmpty() || word.term.contains(query, ignoreCase = true)) &&
                    (filter == "전체" || word.category == filter)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList() // 처음에 DB를 읽어올 동안 잠깐 보여줄 빈 리스트
    )

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun updateFilter(filter: String) {
        selectedFilter.value = filter
    }
}