package com.now.naaga.presentation.adventureresult

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.now.domain.model.AdventureResult
import com.now.domain.model.AdventureResultType
import com.now.naaga.R
import com.now.naaga.data.NaagaThrowable
import com.now.naaga.data.repository.DefaultAdventureRepository
import com.now.naaga.data.repository.DefaultRankRepository
import com.now.naaga.databinding.ActivityAdventureResultBinding
import com.now.naaga.presentation.beginadventure.BeginAdventureActivity

class AdventureResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdventureResultBinding
    private lateinit var viewModel: AdventureResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdventureResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        viewModel.fetchGameResult(getIntentData())
        viewModel.fetchMyRank()
        subscribeObserving()
        setClickListeners()
    }

    private fun getIntentData(): Long {
        return intent.getLongExtra(GAME_ID, -1)
    }

    private fun initViewModel() {
        val adventureRepository = DefaultAdventureRepository()
        val rankRepository = DefaultRankRepository()
        val factory = AdventureResultFactory(adventureRepository, rankRepository)
        viewModel = ViewModelProvider(this, factory)[AdventureResultViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun subscribeObserving() {
        viewModel.adventureResult.observe(this) { adventureResult ->
            setResultType(adventureResult)
            setPhoto(adventureResult.destination.image)
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            if (NaagaThrowable.ServerConnectFailure().userMessage == errorMessage) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setResultType(adventureResult: AdventureResult) {
        when (adventureResult.resultType) {
            AdventureResultType.SUCCESS -> setSuccessTypeView(adventureResult.destination.name)
            AdventureResultType.FAIL -> setFailTypeView()
            AdventureResultType.NONE -> {
                Toast.makeText(this, MESSAGE_IN_RESULT_TYPE_NONE, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setSuccessTypeView(destinationName: String) {
        binding.ivAdventureResultStamp.setImageResource(R.drawable.ic_success)
        binding.tvAdventureResultDestination.text = destinationName
    }

    private fun setFailTypeView() {
        binding.ivAdventureResultStamp.setImageResource(R.drawable.ic_fail)
        binding.tvAdventureResultDestination.text = getString(R.string.adventureResult_fail_destination_name)
    }

    private fun setPhoto(imageUrl: String) {
        Glide.with(binding.ivAdventureResultPhoto)
            .load(imageUrl)
            .fallback(R.drawable.ic_none_photo)
            .error(R.drawable.ic_none_photo)
            .into(binding.ivAdventureResultPhoto)
    }

    private fun setClickListeners() {
        binding.btnAdventureResultReturn.setOnClickListener {
            startActivity(BeginAdventureActivity.getIntent(this))
        }
    }

    companion object {
        private const val GAME_ID = "GAME_ID"
        private const val MESSAGE_IN_RESULT_TYPE_NONE = "네트워크에 문제가 생겼습니다."

        fun getIntentWithGameId(context: Context, gameId: Long): Intent {
            return Intent(context, AdventureResultActivity::class.java).apply {
                putExtra(GAME_ID, gameId)
            }
        }
    }
}
