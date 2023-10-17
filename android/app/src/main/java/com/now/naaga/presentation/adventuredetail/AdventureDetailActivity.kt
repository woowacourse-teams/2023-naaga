package com.now.naaga.presentation.adventuredetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.tabs.TabLayoutMediator
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.databinding.ActivityAdventureDetailBinding
import com.now.naaga.presentation.adventuredetail.viewpager.ViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdventureDetailActivity : AppCompatActivity(), AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: ActivityAdventureDetailBinding
    private val viewModel: AdventureDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdventureDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setClickListeners()
        subscribe()
    }

    private fun initView() {
        viewModel.fetchReadLetter(1L)
        viewModel.fetchWriteLetter(1L)
    }

    private fun setClickListeners() {
        binding.ivAdventureDetailBack.setOnClickListener { finish() }
    }

    private fun subscribe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { adventureDetailUiState ->
                    when (adventureDetailUiState) {
                        is AdventureDetailUiState.Loading, is AdventureDetailUiState.Error -> Unit
                        is AdventureDetailUiState.Success -> initViewPager(adventureDetailUiState)
                    }
                }
            }
        }
    }

    private fun initViewPager(adventureDetailUiState: AdventureDetailUiState.Success) {
        binding.vpAdventureDetail.adapter = ViewPagerAdapter(
            listOf(
                adventureDetailUiState.readLetters,
                adventureDetailUiState.writeLetters,
            ),
        )

        TabLayoutMediator(binding.tlAdventureDetail, binding.vpAdventureDetail) { tab, position ->
            when (position) {
                0 -> tab.text = "읽은 편지"
                1 -> tab.text = "등록한 편지"
            }
        }.attach()
    }

    companion object {
        private const val GAME_ID = "GAME_ID"

        fun getIntentWithId(context: Context, gameId: Long): Intent {
            return Intent(context, AdventureDetailActivity::class.java).apply {
                putExtra(GAME_ID, gameId)
            }
        }
    }
}
