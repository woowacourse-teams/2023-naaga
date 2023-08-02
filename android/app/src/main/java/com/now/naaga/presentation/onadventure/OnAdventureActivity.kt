package com.now.naaga.presentation.onadventure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Coordinate
import com.now.domain.model.Game
import com.now.domain.model.Hint
import com.now.naaga.R
import com.now.naaga.databinding.ActivityOnAdventureBinding
import com.now.naaga.presentation.adventureresult.AdventureResultActivity
import com.now.naaga.presentation.uimodel.mapper.toDomain
import com.now.naaga.presentation.uimodel.mapper.toUi
import com.now.naaga.presentation.uimodel.model.GameUiModel
import com.now.naaga.util.getParcelable

class OnAdventureActivity : AppCompatActivity(), NaverMapSettingDelegate by DefaultNaverMapSettingDelegate() {
    private lateinit var binding: ActivityOnAdventureBinding
    private lateinit var viewModel: OnAdventureViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setNaverMap(this, R.id.fcv_onAdventure_map)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, OnAdventureViewModel.Factory)[OnAdventureViewModel::class.java]
        binding = ActivityOnAdventureBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)

        subscribe()
        setOnMapReady { setLocationChangeListener() }
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.ivOnAdventureGiveUp.setOnClickListener {
            showGiveUpDialog()
        }
        binding.ivOnAdventurePhoto.setOnClickListener {
            showPolaroidDialog()
        }
        binding.ivOnAdventureHint.setOnClickListener {
            showHintDialog()
        }
    }

    private fun setLocationChangeListener() {
        naverMap.addOnLocationChangeListener { location ->
            val coordinate = Coordinate(location.latitude, location.longitude)
            viewModel.calculateDistance(coordinate)
            viewModel.myCoordinate.value = coordinate
            viewModel.startCoordinate.setValue(coordinate)
        }
    }

    private fun subscribe() {
        viewModel.startCoordinate.observe(this) {
            beginAdventure(it)
        }
        viewModel.adventure.observe(this) {
            isAdventureDone(it.adventureStatus)
        }
        viewModel.destination.observe(this) {
            addDestinationMarker(it.coordinate)
        }
        viewModel.lastHint.observe(this) {
            drawHintMarker(it)
        }
        viewModel.failure.observe(this) {
            controlException(it)
        }
    }

    private fun beginAdventure(coordinate: Coordinate) {
        val existingAdventure = intent.getParcelable(ADVENTURE, GameUiModel::class.java)?.toDomain()

        if (existingAdventure == null) {
            viewModel.beginAdventure(coordinate)
            return
        }
        viewModel.setAdventure(existingAdventure)
    }

    private fun isAdventureDone(status: AdventureStatus) {
        if (status == AdventureStatus.DONE) {
            val intent = Intent(this, AdventureResultActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun drawHintMarker(hint: Hint) {
        addHintMarker(hint)
    }

    private fun controlException(throwable: Throwable) {
        fun Context.shorToast(@StringRes message: Int) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        when (throwable) {
            is AdventureThrowable.EndAdventureFailure -> shorToast(R.string.onAdventure_retry)
            is AdventureThrowable.GiveUpAdventureFailure -> shorToast(R.string.onAdventure_error_retry)
            is AdventureThrowable.HintFailure -> shorToast(R.string.onAdventure_no_more_hint)
            is AdventureThrowable.UnExpectedFailure -> shorToast(R.string.onAdventure_unexpected_error)
            is AdventureThrowable.BeginAdventureFailure -> {
                shorToast(R.string.onAdventure_begin_error)
                finish()
            }
        }
    }

    private fun showGiveUpDialog() {
        val fragment: Fragment? = supportFragmentManager.findFragmentByTag(GIVE_UP)
        if (fragment == null) {
            NaagaAlertDialog.Builder(this).buildGiveUpDialog().apply {
                setPositiveButton { dismiss() }
                setNegativeButton {
                    viewModel.giveUpAdventure()
                    dismiss()
                }
            }.show(supportFragmentManager, GIVE_UP)
        } else {
            (fragment as DialogFragment).dialog?.show()
        }
    }

    private fun showHintDialog() {
        val remainingHintCount: Int =
            OnAdventureViewModel.MAX_HINT_COUNT - (viewModel.adventure.value?.hints?.size ?: 0)
        NaagaAlertDialog
            .Builder(this)
            .buildHintUseDialog(remainingHintCount)
            .apply {
                setPositiveButton { viewModel.openHint(); dismiss() }
                setNegativeButton { dismiss() }
            }.show(supportFragmentManager, HINT)
    }

    private fun showPolaroidDialog() {
        val image = viewModel.destination.value?.image ?: return
        val fragment: Fragment? = supportFragmentManager.findFragmentByTag(DESTINATION_PHOTO)
        if (fragment == null) {
            PolaroidDialog.makeDialog(image).show(supportFragmentManager, DESTINATION_PHOTO)
        } else {
            (fragment as DialogFragment).dialog?.show()
        }
    }

    companion object {
        private const val DESTINATION_PHOTO = "DESTINATION_PHOTO"
        private const val GIVE_UP = "GIVE_UP"
        private const val ADVENTURE = "ADVENTURE"
        private const val HINT = "HINT"

        fun getIntent(context: Context): Intent {
            return Intent(context, OnAdventureActivity::class.java)
        }

        fun getIntentWithAdventure(context: Context, adventure: Game): Intent {
            return Intent(context, OnAdventureActivity::class.java).apply {
                putExtra(ADVENTURE, adventure.toUi())
            }
        }
    }
}
