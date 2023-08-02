package com.now.naaga.presentation.adventurehistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.now.domain.model.AdventureResult
import com.now.naaga.databinding.ActivityAdventureHistoryBinding
import com.now.naaga.presentation.adventurehistory.recyclerview.AdventureHistoryAdapter

class AdventureHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdventureHistoryBinding
    private lateinit var viewModel: AdventureHistoryViewModel
    private val historyAdapter = AdventureHistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdventureHistoryBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)

        initViewModel()
        initRecyclerView()
        viewModel.fetchHistories()
        subscribeObserving()
        setClickListeners()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, AdventureHistoryViewModel.Factory)[AdventureHistoryViewModel::class.java]
    }

    private fun initRecyclerView() {
        binding.rvAdventureHistoryVisitedPlaces.apply {
            adapter = historyAdapter
            setHasFixedSize(true)
        }
    }

    private fun subscribeObserving() {
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
