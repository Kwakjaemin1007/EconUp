package com.example.econup.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _todayCount = MutableStateFlow(15)
    val todayCount: StateFlow<Int> = _todayCount.asStateFlow()

    private val _accuracy = MutableStateFlow(85)
    val accuracy: StateFlow<Int> = _accuracy.asStateFlow()

    private val _streakDays = MutableStateFlow(4)
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

    // 카테고리명과 진행도(%)
    private val _categoryProgress = MutableStateFlow(
        mapOf("거시경제" to 40, "미시경제" to 70, "금융/투자" to 20)
    )
    val categoryProgress: StateFlow<Map<String, Int>> = _categoryProgress.asStateFlow()
}