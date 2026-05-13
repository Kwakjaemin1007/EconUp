package com.example.econup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.econup.ui.components.BottomNavBar
import com.example.econup.ui.home.HomeScreen
import com.example.econup.ui.wordlist.WordListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 전체 다크 테마 강제 적용
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    EconUpApp()
                }
            }
        }
    }
}

@Composable
fun EconUpApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("category") { /* CategoryScreen(navController) */ }
            composable("learn/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                /* LearnScreen(category) */
            }
            composable("wordlist") { WordListScreen() }
            composable("news") { /* NewsScreen() */ }
            composable("profile") { /* ProfileScreen() */ }
        }
    }
}