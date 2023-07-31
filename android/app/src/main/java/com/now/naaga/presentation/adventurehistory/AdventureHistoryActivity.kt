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
        setContentView(binding.root)
        initViewModel()
        initRecyclerView()
        viewModel.fetchHistories()
        startObserving()
        setClickListeners()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[AdventureHistoryViewModel::class.java]
        binding.lifecycleOwner = this
    }

    private fun initRecyclerView() {
        binding.rvAdventureHistoryVisitedPlaces.apply {
            adapter = historyAdapter
            itemAnimator = null
            setHasFixedSize(true)
        }
    }

    private fun startObserving() {
        viewModel.places.observe(this) { places ->
            updateHistory(places)
        }
    }

    private fun setClickListeners() {
        binding.ivAdventureHistoryBack.setOnClickListener {
            finish()
        }
    }

    private fun updateHistory(places: List<AdventureResult>) {
        historyAdapter.submitList(places)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, AdventureHistoryActivity::class.java)
        }
    }
}
