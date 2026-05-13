package com.example.econup.ui.learn

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.econup.ui.components.FlashCard

@Composable
fun LearnScreen(
    navController: NavController,
    viewModel: LearnViewModel,
    category: String
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 화면에 처음 진입할 때 해당 카테고리의 단어들을 로드합니다.
    LaunchedEffect(category) {
        viewModel.loadCategory(category)
    }

    // 데이터 로딩 중 화면
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // 학습을 모두 마쳤을 때 화면
    if (uiState.isFinished || uiState.words.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎉 학습 완료!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("목록으로 돌아가기")
                }
            }
        }
        return
    }

    // 현재 학습 중인 단어
    val currentWord = uiState.words[uiState.currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 상단 진행도 표시바
        Text(
            text = "$category 학습 중 (${uiState.currentIndex + 1} / ${uiState.words.size})",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (uiState.currentIndex + 1).toFloat() / uiState.words.size.toFloat() },
            modifier = Modifier.fillMaxWidth().height(8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 2. 플래시카드 (터치 시 뒤집힘)
        FlashCard(
            word = currentWord,
            isFlipped = uiState.isFlipped,
            onClick = { viewModel.flip() }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 3. 4지선다 퀴즈 영역
        Text("알맞은 뜻을 선택하세요", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        uiState.quizOptions.forEach { option ->
            Button(
                onClick = {
                    val isCorrect = (option == currentWord.definition)
                    viewModel.submitAnswer(currentWord.id, isCorrect)

                    // 정답/오답 토스트 메시지 피드백
                    if (isCorrect) {
                        Toast.makeText(context, "정답입니다! 🎉", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "오답입니다 😢", Toast.LENGTH_SHORT).show()
                    }

                    viewModel.next() // 다음 문제로 이동
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(
                    text = option,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}