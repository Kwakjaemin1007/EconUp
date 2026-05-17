package com.example.econup.data.network

// 네이버 뉴스 API 응답 구조를 참고한 모델입니다.
data class NewsResponse(
    val items: List<NewsItem>
)

data class NewsItem(
    val title: String,
    val description: String,
    val link: String,
    val pubDate: String
)