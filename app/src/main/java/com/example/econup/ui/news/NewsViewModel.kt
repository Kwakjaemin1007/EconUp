package com.example.econup.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.econup.data.entity.EconomyWord
import com.example.econup.data.network.NewsItem
import com.example.econup.data.repository.NewsRepository
import com.example.econup.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// 뉴스와 해당 뉴스에 포함된 경제 용어를 묶어주는 데이터 클래스
data class NewsUiModel(
    val news: NewsItem,
    val relatedWords: List<EconomyWord>
)

class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val wordRepository: WordRepository
) : ViewModel() {

    private val _newsList = MutableStateFlow<List<NewsUiModel>>(emptyList())
    val newsList: StateFlow<List<NewsUiModel>> = _newsList.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadNewsAndExtractWords()
    }

    private fun loadNewsAndExtractWords() {
        viewModelScope.launch {
            _isLoading.value = true

            val rawNews = newsRepository.fetchNews()
            val allWords = wordRepository.getAllWords().first() // DB의 전체 단어 가져오기

            // 뉴스 내용(title + description)에 단어가 포함되어 있는지 검사
            val combinedList = rawNews.map { newsItem ->
                val fullText = newsItem.title + newsItem.description
                val matchedWords = allWords.filter { word ->
                    fullText.contains(word.term, ignoreCase = true)
                }
                NewsUiModel(news = newsItem, relatedWords = matchedWords)
            }

            _newsList.value = combinedList
            _isLoading.value = false
        }
    }
}