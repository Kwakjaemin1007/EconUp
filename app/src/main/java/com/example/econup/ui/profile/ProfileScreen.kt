package com.example.econup.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. 프로필 헤더 (아이콘 + 닉네임)
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "프로필", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "성장하는 경제학도", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(text = "가입일: 2024.05.14", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // 2. 학습 통계 카드
        item {
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(title = "학습한 단어", value = "${uiState.learnedWords}개")
                    Divider(modifier = Modifier.height(40.dp).width(1.dp))
                    StatItem(title = "누적 정답률", value = "${uiState.accuracy}%")
                }
            }
        }

        // 3. 획득한 배지
        item {
            Text(text = "🏆 나의 배지", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.badges) { badge ->
                    BadgeCard(emoji = badge.first, name = badge.second)
                }
            }
        }

        // 4. 설정 메뉴 리스트
        item {
            Text(text = "⚙️ 설정", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    SettingMenuItem(icon = Icons.Default.Notifications, title = "알림 설정")
                    Divider()
                    SettingMenuItem(icon = Icons.Default.Settings, title = "앱 설정")
                }
            }
        }
    }
}

@Composable
fun StatItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun BadgeCard(emoji: String, name: String) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SettingMenuItem(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "이동", tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}