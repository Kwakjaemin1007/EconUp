package com.example.econup.ui.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.econup.data.entity.EconomyWord
import com.example.econup.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LearnUiState(
    val words: List<EconomyWord> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val quizOptions: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val isFinished: Boolean = false
)

class LearnViewModel(
    private val wordRepository: WordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearnUiState())
    val uiState: StateFlow<LearnUiState> = _uiState.asStateFlow()

    // 퀴즈의 오답 보기를 추출하기 위해 전체 단어 풀을 저장하는 변수
    private var allWordsPool: List<EconomyWord> = emptyList()

    init {
        viewModelScope.launch {
            allWordsPool = wordRepository.getAllWords().first()
        }
    }

    fun loadCategory(category: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 카테고리에 해당하는 단어를 Flow에서 가져오고 섞음
            val words = wordRepository.getWordsByCategory(category).first().shuffled()

            _uiState.update {
                it.copy(
                    words = words,
                    currentIndex = 0,
                    isFlipped = false,
                    isLoading = false,
                    isFinished = words.isEmpty()
                )
            }
            generateQuizOptions()
        }
    }

    fun flip() {
        _uiState.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun next() {
        val nextIndex = _uiState.value.currentIndex + 1
        if (nextIndex < _uiState.value.words.size) {
            _uiState.update {
                it.copy(
                    currentIndex = nextIndex,
                    isFlipped = false
                )
            }
            generateQuizOptions()
        } else {
            _uiState.update { it.copy(isFinished = true) }
        }
    }

    fun submitAnswer(wordId: String, isCorrect: Boolean) {
        viewModelScope.launch {
            wordRepository.recordAnswer(wordId, isCorrect)
            // 정답 여부에 따른 UI 피드백 로직 추가 가능 (예: 토스트, 스낵바 State)
        }
    }

    private fun generateQuizOptions() {
        val state = _uiState.value
        if (state.words.isEmpty() || state.isFinished) return

        val currentWord = state.words[state.currentIndex]
        val correctAnswer = currentWord.definition

        // 현재 단어를 제외한 풀에서 랜덤으로 3개의 오답 정의 추출
        val wrongAnswers = allWordsPool
            .filter { it.id != currentWord.id }
            .map { it.definition }
            .shuffled()
            .take(3)

        // 정답 1개 + 오답 3개 합친 뒤 섞기
        val options = (wrongAnswers + correctAnswer).shuffled()

        _uiState.update { it.copy(quizOptions = options) }
    }
}