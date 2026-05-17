package com.example.econup.data.repository

import com.example.econup.data.network.NewsApiService
import com.example.econup.data.network.NewsItem

class NewsRepository(
    private val apiService: NewsApiService? = null
) {
    suspend fun fetchNews(): List<NewsItem> {
        // API 함수를 찾지 않고, 무조건 예쁜 더미 데이터를 반환하여 화면을 보여줍니다.
        return getMockNews()
    }

    // 💡 테스트를 위한 가짜 뉴스 (우리 단어장에 있을 법한 단어 포함)
    private fun getMockNews(): List<NewsItem> {
        return listOf(
            NewsItem(
                title = "미 연준, 기준금리 0.25%p 인상... 인플레이션 우려 여전",
                description = "미국 연방준비제도(Fed)가 물가 안정을 위해 다시 한번 금리를 올렸습니다. 시장은 스태그플레이션 진입을 경계하고 있습니다.",
                link = "",
                pubDate = "방금 전"
            ),
            NewsItem(
                title = "비트코인 등 암호화폐 일제히 상승장, 디파이(DeFi) 생태계 활기",
                description = "주요 코인들이 강세를 보이며 블록체인 기반 금융인 디파이 서비스에 자금이 몰리고 있습니다.",
                link = "",
                pubDate = "1시간 전"
            ),
            NewsItem(
                title = "부동산 시장 '빙하기'... 주택담보대출 이자 부담에 거래 절벽",
                description = "DSR 규제와 금리 인상의 여파로 아파트 거래량이 역대 최저치를 기록했습니다.",
                link = "",
                pubDate = "3시간 전"
            )
        )
    }
}