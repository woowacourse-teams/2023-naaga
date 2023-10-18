package com.now.naaga.presentation.adventureresult

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.now.domain.model.AdventureResult
import com.now.domain.model.type.AdventureResultType
import com.now.naaga.R
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.firebase.analytics.RESULT_RESULT_RETURN
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.databinding.ActivityAdventureResultBinding
import com.now.naaga.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdventureResultActivity : AppCompatActivity(), AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: ActivityAdventureResultBinding
    private val viewModel: AdventureResultViewModel by viewModels()

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
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun subscribe() {
        viewModel.adventureResult.observe(this) { adventureResult ->
            setResultType(adventureResult)
            setPhoto(adventureResult.destination.image)
            viewModel.fetchPreference()
        }

        viewModel.throwable.observe(this) { throwable: DataThrowable ->
            when (throwable.code) {
                DataThrowable.NETWORK_THROWABLE_CODE -> { showToast(getString(R.string.network_error_message)) }
            }
        }

        viewModel.preference.observe(this) {
            binding.customAdventureResultPreference.updatePreference(it.state)
            binding.customAdventureResultPreference.likeCount = it.likeCount.value
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
        binding.ivAdventureResultStamp.setImageResource(R.drawable.ic_success_label)
        binding.tvAdventureResultDestination.text = destinationName
    }

    private fun setFailTypeView() {
        binding.ivAdventureResultStamp.setImageResource(R.drawable.ic_fail_label)
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

        binding.customAdventureResultPreference.setPreferenceClickListener {
            viewModel.changePreference(it)
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
