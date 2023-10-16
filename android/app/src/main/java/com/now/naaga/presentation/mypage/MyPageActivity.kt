package com.now.naaga.presentation.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.now.domain.model.Statistics
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.firebase.analytics.MYPAGE_GO_RESULTS
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.databinding.ActivityMyPageBinding
import com.now.naaga.presentation.adventurehistory.AdventureHistoryActivity
import com.now.naaga.presentation.mypage.statistics.MyPageStatisticsAdapter
import com.now.naaga.presentation.uimodel.model.StatisticsUiModel
import com.now.naaga.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageActivity : AppCompatActivity(), AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: ActivityMyPageBinding
    private val viewModel: MyPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerAnalytics(this.lifecycle)
        initViewModel()
        setClickListeners()
        subscribe()
        fetchData()
    }

    private fun initViewModel() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun setClickListeners() {
        binding.ivMypageBack.setOnClickListener {
            finish()
        }
        binding.btnMypageAdventureHistory.setOnClickListener {
            logClickEvent(getViewEntryName(it), MYPAGE_GO_RESULTS)
            val intent = AdventureHistoryActivity.getIntent(this)
            startActivity(intent)
        }
    }

    private fun fetchData() {
        viewModel.fetchRank()
        viewModel.fetchStatistics()
        viewModel.fetchPlaces()
    }

    private fun subscribe() {
        viewModel.statistics.observe(this) { statistics ->
            initRecyclerView(statistics)
        }
        viewModel.places.observe(this) { places ->
            val placesUiModels = places.map { it.toUiModel() }
            binding.customGridMypagePlaces.initContent(placesUiModels)
        }
        viewModel.throwable.observe(this) { throwable: DataThrowable ->
            when (throwable.code) {
                DataThrowable.NETWORK_THROWABLE_CODE -> { showToast(throwable.message ?: "") }
            }
        }
    }

    private fun initRecyclerView(statistics: Statistics) {
        val statisticsUiModels = mutableListOf(
            StatisticsUiModel.getSuccessAdventureStatisticsModel(statistics.successCount),
            StatisticsUiModel.getFailAdventureStatisticsModel(statistics.failureCount),
            StatisticsUiModel.getAllAdventureStatisticsModel(statistics.adventureCount),
        )
        val adapter = MyPageStatisticsAdapter(statisticsUiModels)
        binding.rvMypageStatistics.adapter = adapter
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MyPageActivity::class.java)
        }
    }
}
