package com.example.econup.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel // = viewModel() 제거됨 (NavHost에서 주입)
) {
    val todayCount by viewModel.todayCount.collectAsState()
    val accuracy by viewModel.accuracy.collectAsState()
    val streakDays by viewModel.streakDays.collectAsState()
    val categoryProgress by viewModel.categoryProgress.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "🔥 나의 학습 현황", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        // 스트릭 카드 (7일 표시)
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("연속 학습: $streakDays 일째!", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (i in 1..7) {
                            val color = if (i <= streakDays) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            Box(modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = color
                                )
                            }
                        }
                    }
                }
            }
        }

        // 통계 3개
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatCard("오늘 학습", "$todayCount 단어", Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                StatCard("정답률", "$accuracy%", Modifier.weight(1f))
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "📚 카테고리별 진행도", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        // 카테고리 진행도 리스트
        items(categoryProgress.entries.toList()) { entry ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("category") } // 탭 시 category 이동
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = entry.key, fontWeight = FontWeight.Bold)
                    Text(text = "${entry.value}%")
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}