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
import com.now.naaga.data.firebase.analytics.ADVENTURE_RESULT
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.firebase.analytics.RESULT_RESULT_RETURN
import com.now.naaga.databinding.ActivityAdventureResultBinding

class AdventureResultActivity : AppCompatActivity(), AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: ActivityAdventureResultBinding
    private lateinit var viewModel: AdventureResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdventureResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerAnalytics(this.lifecycle)
        initViewModel()
        viewModel.fetchGameResult(getIntentData() ?: return finish())
        viewModel.fetchMyRank()
        subscribe()
        setClickListeners()
    }

    private fun getIntentData(): Long? {
        val id = intent.getLongExtra(GAME_ID, -1)
        return if (id == -1L) {
            null
        } else {
            id
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, AdventureResultViewModel.Factory)[AdventureResultViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun subscribe() {
        viewModel.adventureResult.observe(this) { adventureResult ->
            setResultType(adventureResult)
            setPhoto(adventureResult.destination.image)
        }

        viewModel.throwable.observe(this) { throwable ->
            Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
            logServerError(ADVENTURE_RESULT, throwable.code, throwable.message.toString())
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
            logClickEvent(getViewEntryName(it), RESULT_RESULT_RETURN)
            finish()
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
