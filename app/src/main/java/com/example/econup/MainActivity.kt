package com.example.econup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.econup.data.db.AppDatabase
import com.example.econup.data.db.ProgressDao
import com.example.econup.data.repository.NewsRepository
import com.example.econup.data.repository.WordRepository
import com.example.econup.ui.category.CategoryScreen
import com.example.econup.ui.category.CategoryViewModel
import com.example.econup.ui.components.BottomNavBar
import com.example.econup.ui.home.HomeScreen
import com.example.econup.ui.home.HomeViewModel
import com.example.econup.ui.learn.LearnScreen
import com.example.econup.ui.learn.LearnViewModel
import com.example.econup.ui.news.NewsScreen
import com.example.econup.ui.news.NewsViewModel
import com.example.econup.ui.profile.ProfileScreen
import com.example.econup.ui.profile.ProfileViewModel
import com.example.econup.ui.wordlist.WordListScreen
import com.example.econup.ui.wordlist.WordListViewModel
import kotlin.jvm.java

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 앱 전체에서 사용할 DB와 Repository 준비
        val database = AppDatabase.getInstance(this)
        val wordRepository = WordRepository(database.wordDao(), database.progressDao())
        val progressDao = database.progressDao()

        // 뷰모델 팩토리 준비 (Hilt 미사용 시 필수)
        val viewModelFactory = AppViewModelFactory(wordRepository, progressDao)

        setContent {
            EconUpApp(viewModelFactory)
        }
    }
}

@Composable
fun EconUpApp(viewModelFactory: ViewModelProvider.Factory) {
    val navController = rememberNavController()

    // 현재 화면 추적하여 퀴즈 화면(learn)에서는 바텀 네비게이션 숨기기
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute?.startsWith("learn") != true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home", // 앱 시작 화면을 home으로 변경!
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. 홈 화면 연결
            composable("home") {
                val viewModel: HomeViewModel = viewModel(factory = viewModelFactory)
                HomeScreen(navController = navController, viewModel = viewModel)
            }
            // 3. 단어장 화면
            composable("wordlist") {
                val viewModel: WordListViewModel = viewModel(factory = viewModelFactory)
                WordListScreen(navController = navController, viewModel = viewModel)
            }
            composable("news") {
                val viewModel: NewsViewModel = viewModel(factory = viewModelFactory)
                NewsScreen(viewModel = viewModel)
            }
            composable("profile") {
                val viewModel: ProfileViewModel = viewModel(factory = viewModelFactory)
                ProfileScreen(viewModel = viewModel) }

            // 1. 카테고리 화면
            composable("category") {
                val viewModel: CategoryViewModel = viewModel(factory = viewModelFactory)
                CategoryScreen(navController = navController, viewModel = viewModel)
            }

            // 2. 학습(퀴즈) 화면
            composable(
                route = "learn/{categoryName}",
                arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
            ) { backStackEntry ->
                val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
                val viewModel: LearnViewModel = viewModel(factory = viewModelFactory)
                LearnScreen(navController = navController, viewModel = viewModel, category = categoryName)
            }
        }
    }
}

// 뷰모델 생성기 (팩토리)
class AppViewModelFactory(
    private val wordRepository: WordRepository,
    private val progressDao: ProgressDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(wordRepository, progressDao) as T
        }
        if (modelClass.isAssignableFrom(LearnViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LearnViewModel(wordRepository) as T
        }
        if (modelClass.isAssignableFrom(WordListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WordListViewModel(wordRepository, progressDao) as T
        }
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(wordRepository, progressDao) as T
        }
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            val newsRepository = NewsRepository(null)
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(newsRepository, wordRepository) as T
        }
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(wordRepository, progressDao) as T
        } // 👉 Profile 괄호가 여기서 닫혀야 합니다!

        // 👉 에러 던지는 코드는 모든 if문이 끝난 뒤 제일 마지막에 있어야 합니다!
        throw IllegalArgumentException("Unknown ViewModel class")
    } // create 함수 닫기
}