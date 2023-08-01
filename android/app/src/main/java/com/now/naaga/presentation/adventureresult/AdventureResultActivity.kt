package com.now.naaga.presentation.adventureresult

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.now.naaga.data.repository.DefaultAdventureRepository
import com.now.naaga.databinding.ActivityAdventureResultBinding
import com.now.naaga.presentation.onadventure.OnAdventureActivity

class AdventureResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdventureResultBinding
    private lateinit var viewModel: AdventureResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdventureResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        viewModel.checkAdventureResultState(AdventureResultViewModel.TEMP_SUCCESS_CODE)
        binding.viewModel = viewModel
    }

    private fun getIntentData(): String? {
        return intent.getStringExtra(GAME_ID)
    }

    private fun initViewModel() {
        val repository = DefaultAdventureRepository()
        val factory = AdventureResultFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AdventureResultViewModel::class.java]
    }

    companion object {
        private const val GAME_ID = "GAME_ID"

        fun getIntentWithGameId(context: Context, gameId: Long): Intent {
            return Intent(context, OnAdventureActivity::class.java).apply {
                putExtra(GAME_ID, gameId)
            }
        }
    }
}
