package com.now.naaga.presentation.mypage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.now.naaga.data.repository.DefaultRankRepository
import com.now.naaga.data.repository.DefaultStatisticsRepository
import com.now.naaga.databinding.ActivityMyPageBinding

class MyPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyPageBinding
    private lateinit var viewModel: MyPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        setClickListeners()
        fetchData()
        subscribe()
        binding.customGridPlaces.initContent(getPlaceData())
    }

    private fun initViewModel() {
        val rankRepository = DefaultRankRepository()
        val statisticsRepository = DefaultStatisticsRepository()
        val factory = MyPageFactory(rankRepository, statisticsRepository)
        viewModel = ViewModelProvider(this, factory)[MyPageViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun setClickListeners() {
        binding.ivMypageBack.setOnClickListener {
            finish()
        }
    }

    private fun fetchData() {
        viewModel.fetchRank()
        viewModel.fetchStatistics()
    }

    private fun subscribe() {
        viewModel.statistics.observe(this) { statistics ->
            binding.customGridMypageStatistics.initContent(statistics.toUiModel(this))
        }
    }

    private fun getPlaceData(): List<MyPagePlaceUiModel> = listOf(
        MyPagePlaceUiModel(
            "https://i.namu.wiki/i/weOyvjMO4Pv2TzZPXdlaTj3zzbQglcDeBUPrVf9WADE8899wqRWrl1WYknSDDr7BC-lk2WzTdWfBKvDAKChUew.webp", // ktlint-disable max-line-length
            "상암 월드컵 경기장",
        ),
        MyPagePlaceUiModel(
            "https://i.namu.wiki/i/weOyvjMO4Pv2TzZPXdlaTj3zzbQglcDeBUPrVf9WADE8899wqRWrl1WYknSDDr7BC-lk2WzTdWfBKvDAKChUew.webp", // ktlint-disable max-line-length
            "상암 월드컵 경기장",
        ),
        MyPagePlaceUiModel(
            "https://file.notion.so/f/s/82dd44ef-49b1-4c14-958c-1dc7b8f06621/Untitled.png?id=59a73f79-ee49-4565-847c-d950538d4b22&table=block&spaceId=3a3e71f9-ddeb-4615-ba95-60d1f58c8cb7&expirationTimestamp=1690848000000&signature=xFY1FVE8oEInN8yjI1wnm2oxjkXziuccLc8CLsfKPP0&downloadName=Untitled.png", // ktlint-disable max-line-length
            "삼전도비",
        ),
    )

    private fun getStatisticsData(): List<MyPageStatisticsUiModel> = listOf(
        MyPageStatisticsUiModel(10, "회", "전체 모험"),
        MyPageStatisticsUiModel(8, "회", "모험 성공"),
        MyPageStatisticsUiModel(2, "회", "모험 실패"),
        MyPageStatisticsUiModel(600, "분", "총 모험 시간"),
        MyPageStatisticsUiModel(3000, "미터", "총 모험 직경"),
    )
}
