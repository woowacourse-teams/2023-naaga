package com.now.naaga.presentation.adventurehistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.now.domain.model.AdventureResult
import com.now.naaga.databinding.ActivityAdventureHistoryBinding
import com.now.naaga.presentation.adventurehistory.recyclerview.AdventureHistoryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdventureHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdventureHistoryBinding
    private val viewModel: AdventureHistoryViewModel by viewModels()
    private val historyAdapter = AdventureHistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdventureHistoryBinding.inflate(layoutInflater)

        setContentView(binding.root)
        initRecyclerView()
        viewModel.fetchHistories()
        subscribe()
        setClickListeners()
    }

    private fun initRecyclerView() {
        binding.rvAdventureHistoryVisitedPlaces.apply {
            adapter = historyAdapter
            setHasFixedSize(true)
        }
    }

    private fun subscribe() {
        viewModel.adventureResults.observe(this) { adventureResults ->
            updateHistory(adventureResults)
        }
    }

    private fun setClickListeners() {
        binding.ivAdventureHistoryBack.setOnClickListener {
            finish()
        }
    }

    private fun updateHistory(adventureResults: List<AdventureResult>) {
        historyAdapter.submitList(adventureResults)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, AdventureHistoryActivity::class.java)
        }
    }
}
