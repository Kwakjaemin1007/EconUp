package com.example.econup.ui.wordlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun WordListScreen(
    navController: NavController,
    viewModel: WordListViewModel // = viewModel() 제거됨
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val words by viewModel.filteredWords.collectAsState()

    val filters = listOf("전체", "학습완료", "북마크", "거시경제")

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("단어 검색...") },
            singleLine = true
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { viewModel.updateFilter(filter) },
                    label = { Text(filter) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // key를 지정해주면 스크롤이나 북마크 변경 시 깜빡임이 덜합니다.
            items(words, key = { it.word.id }) { uiModel ->
                WordCard(
                    uiModel = uiModel,
                    onBookmarkClick = { viewModel.toggleBookmark(uiModel.word.id) }
                )
            }
        }
    }
}

@Composable
fun WordCard(
    uiModel: WordUiModel,
    onBookmarkClick: () -> Unit
) {
    val word = uiModel.word

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = word.term, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = word.fullName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // 북마크 여부에 따라 아이콘 및 색상 변경
                IconButton(onClick = onBookmarkClick) {
                    Icon(
                        imageVector = if (uiModel.isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "북마크",
                        tint = if (uiModel.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 카테고리와 학습완료 상태를 칩으로 예쁘게 표시
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(onClick = {}, label = { Text(word.category) })
                if (uiModel.isLearned) {
                    SuggestionChip(onClick = {}, label = { Text("✅ 학습완료") })
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = word.definition, style = MaterialTheme.typography.bodyMedium)
        }
    }
}