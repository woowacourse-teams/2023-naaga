package com.now.naaga.presentation.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.now.naaga.R
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.firebase.analytics.MYPAGE_GO_RESULTS
import com.now.naaga.data.firebase.analytics.MY_PAGE_STATISTICS
import com.now.naaga.databinding.ActivityMyPageBinding
import com.now.naaga.presentation.adventurehistory.AdventureHistoryActivity
import com.now.naaga.presentation.mypage.statistics.MyPageStatisticsAdapter
import com.now.naaga.presentation.uimodel.model.StatisticsUiModel
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
        binding.btnMypageAdventureResults.setOnClickListener {
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
            val list = listOf(
                StatisticsUiModel(R.drawable.ic_success_adventure, "성공 모험", statistics.successCount),
                StatisticsUiModel(R.drawable.ic_fail_adventure, "실패 모험", statistics.failureCount),
                StatisticsUiModel(R.drawable.ic_all_adventure, "전체 모험", statistics.adventureCount),
            )
            val adapter = MyPageStatisticsAdapter(list)
            binding.rvMypageStatistics.adapter = adapter
        }
        viewModel.places.observe(this) { places ->
            val placesUiModel = places.map { it.toUiModel() }
            binding.customGridMypagePlaces.initContent(placesUiModel)
        }
        viewModel.throwable.observe(this) { throwable ->
            Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
            logServerError(MY_PAGE_STATISTICS, throwable.code, throwable.message.toString())
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MyPageActivity::class.java)
        }
    }
}
