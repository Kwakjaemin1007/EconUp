package com.example.econup.ui.news

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.econup.data.entity.EconomyWord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    viewModel: NewsViewModel
) {
    val newsList by viewModel.newsList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 팝업(바텀 시트) 상태 관리
    var selectedWord by remember { mutableStateOf<EconomyWord?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("📰 실시간 경제 뉴스", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        items(newsList) { newsUiModel ->
            NewsCard(
                uiModel = newsUiModel,
                onWordClick = { word ->
                    selectedWord = word
                    showBottomSheet = true
                }
            )
        }
    }

    // 경제 용어 클릭 시 하단에서 올라오는 팝업
    if (showBottomSheet && selectedWord != null) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)
            ) {
                Text(text = selectedWord!!.term, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                    Text(selectedWord!!.category, modifier = Modifier.padding(4.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = selectedWord!!.definition, style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { showBottomSheet = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("확인")
                }
            }
        }
    }
}

@Composable
fun NewsCard(
    uiModel: NewsUiModel,
    onWordClick: (EconomyWord) -> Unit
) {
    val news = uiModel.news
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = news.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = news.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = news.pubDate, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            // 뉴스에 포함된 경제 용어가 있다면 태그(칩)로 표시
            if (uiModel.relatedWords.isNotEmpty()) {
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                Text(text = "💡 연관 경제 용어", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiModel.relatedWords) { word ->
                        AssistChip(
                            onClick = { onWordClick(word) },
                            label = { Text(word.term) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        )
                    }
                }
            }
        }
    }
}