package com.example.econup.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavController) {
    // 탭 메뉴 리스트 (라우트, 제목, 아이콘)
    val items = listOf(
        Triple("home", "홈", Icons.Default.Home),
        Triple("category", "학습", Icons.Default.Category),
        Triple("wordlist", "단어장", Icons.Default.List),
        Triple("news", "뉴스", Icons.Default.Article),
        Triple("profile", "내 정보", Icons.Default.Person)
    )

    NavigationBar {
        // 현재 선택된 화면이 무엇인지 추적
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { (route, title, icon) ->
            NavigationBarItem(
                icon = { Icon(imageVector = icon, contentDescription = title) },
                label = { Text(title) },
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        // 백스택에 화면이 무한히 쌓이는 것 방지
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}