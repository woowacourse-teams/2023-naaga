package com.now.naaga.presentation.adventurehistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.now.naaga.databinding.ActivityAdventureHistoryBinding

class AdventureHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdventureHistoryBinding
    private lateinit var viewModel: AdventureHistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdventureHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        viewModel.fetchHistories()
        startObserving()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[AdventureHistoryViewModel::class.java]
        binding.lifecycleOwner = this
    }

    private fun startObserving() {
        viewModel.places.observe(this) { places ->
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, AdventureHistoryActivity::class.java)
        }
    }
}
