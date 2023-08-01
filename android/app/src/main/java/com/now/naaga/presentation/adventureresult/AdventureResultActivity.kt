package com.now.naaga.presentation.adventureresult

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.now.naaga.data.repository.DefaultRankRepository
import com.now.naaga.data.repository.ThirdDemoAdventureRepository
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
    }

    private fun getIntentData(): Long {
        return intent.getLongExtra(GAME_ID, -1)
    }

    private fun initViewModel() {
        val adventureRepository = ThirdDemoAdventureRepository()
        val rankRepository = DefaultRankRepository()
        val factory = AdventureResultFactory(adventureRepository, rankRepository)
        viewModel = ViewModelProvider(this, factory)[AdventureResultViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    companion object {
        private const val GAME_ID = "GAME_ID"
        private const val MESSAGE_IN_RESULT_TYPE_NONE = "네트워크에 문제가 생겼습니다."

        fun getIntentWithGameId(context: Context, gameId: Long): Intent {
            return Intent(context, OnAdventureActivity::class.java).apply {
                putExtra(GAME_ID, gameId)
            }
        }
    }
}
