package com.now.naaga.presentation.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.now.naaga.data.NaagaThrowable
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.firebase.analytics.MYPAGE_GO_RESULTS
import com.now.naaga.data.repository.DefaultPlaceRepository
import com.now.naaga.data.repository.DefaultRankRepository
import com.now.naaga.data.repository.DefaultStatisticsRepository
import com.now.naaga.databinding.ActivityMyPageBinding
import com.now.naaga.presentation.adventurehistory.AdventureHistoryActivity

class MyPageActivity : AppCompatActivity(), AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: ActivityMyPageBinding
    private lateinit var viewModel: MyPageViewModel

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
        val rankRepository = DefaultRankRepository()
        val statisticsRepository = DefaultStatisticsRepository()
        val placeRepository = DefaultPlaceRepository()
        val factory = MyPageFactory(rankRepository, statisticsRepository, placeRepository)
        viewModel = ViewModelProvider(this, factory)[MyPageViewModel::class.java]
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
            binding.customGridMypageStatistics.initContent(statistics.toUiModel(this))
        }
        viewModel.places.observe(this) { places ->
            val placesUiModel = places.map { it.toUiModel() }
            binding.customGridMypagePlaces.initContent(placesUiModel)
        }
        viewModel.errorMessage.observe(this) { errorMessage ->
            if (NaagaThrowable.ServerConnectFailure().message == errorMessage) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MyPageActivity::class.java)
        }
    }
}
