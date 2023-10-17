package com.now.naaga.presentation.adventuredetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.now.domain.model.AdventureResult
import com.now.naaga.R
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.databinding.ActivityAdventureDetailBinding
import com.now.naaga.presentation.adventuredetail.viewpager.ViewPagerAdapter
import com.now.naaga.presentation.uimodel.model.OpenLetterUiModel
import com.now.naaga.util.extension.repeatOnStarted
import com.now.naaga.util.extension.showSnackbarWithEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdventureDetailActivity : AppCompatActivity(), AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: ActivityAdventureDetailBinding
    private val viewModel: AdventureDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdventureDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gameId = intent.getLongExtra(KET_GAME_ID, 0L)

        initView(gameId)
        setClickListeners()
        subscribe()
    }

    private fun initView(gameId: Long) {
        viewModel.fetchReadLetter(gameId)
        viewModel.fetchWriteLetter(gameId)
        viewModel.fetchAdventureResult(gameId)
    }

    private fun setClickListeners() {
        binding.ivAdventureDetailBack.setOnClickListener { finish() }
    }

    private fun subscribe() {
        repeatOnStarted {
            viewModel.uiState.collect { adventureDetailUiState ->
                when (adventureDetailUiState) {
                    is AdventureDetailUiState.Loading, is AdventureDetailUiState.Error -> Unit
                    is AdventureDetailUiState.Success -> initView(adventureDetailUiState)
                }
            }
        }
        repeatOnStarted {
            viewModel.throwableFlow.collect { event ->
                when (event) {
                    is AdventureDetailViewModel.Event.NetworkExceptionEvent -> showReRequestSnackbar()
                    is AdventureDetailViewModel.Event.LetterExceptionEvent -> showReRequestSnackbar()
                    is AdventureDetailViewModel.Event.GameExceptionEvent -> showReRequestSnackbar()
                }
            }
        }
    }

    private fun showReRequestSnackbar() {
        binding.root.showSnackbarWithEvent(
            message = getString(R.string.snackbar_action_re_request_message),
            actionTitle = getString(R.string.snackbar_action__re_request_title),
        ) { finish() }
    }

    private fun initView(adventureDetailUiState: AdventureDetailUiState.Success) {
        initViewPager(adventureDetailUiState.readLetters, adventureDetailUiState.writeLetters)
        initImage(adventureDetailUiState.adventureResult)
    }

    private fun initViewPager(readLetters: List<OpenLetterUiModel>, writeLetters: List<OpenLetterUiModel>) {
        binding.vpAdventureDetail.adapter = ViewPagerAdapter(listOf(readLetters, writeLetters))

        TabLayoutMediator(binding.tlAdventureDetail, binding.vpAdventureDetail) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.adventure_detail_read_letter)
                1 -> tab.text = getString(R.string.adventure_detail_write_letter)
            }
        }.attach()
    }

    private fun initImage(adventureResult: AdventureResult) {
        Glide.with(binding.ivAdventureDetailPhoto)
            .load(adventureResult.destination.image)
            .error(R.drawable.ic_none_photo)
            .into(binding.ivAdventureDetailPhoto)
    }

    companion object {
        private const val KET_GAME_ID = "GAME_ID"

        fun getIntentWithId(context: Context, gameId: Long): Intent {
            return Intent(context, AdventureDetailActivity::class.java).apply {
                putExtra(KET_GAME_ID, gameId)
            }
        }
    }
}
