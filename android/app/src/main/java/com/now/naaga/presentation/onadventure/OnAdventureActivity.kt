package com.now.naaga.presentation.onadventure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.now.domain.model.Adventure
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Coordinate
import com.now.domain.model.Hint
import com.now.domain.model.letter.ClosedLetter
import com.now.naaga.R
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.firebase.analytics.ON_ADVENTURE_END_ADVENTURE
import com.now.naaga.data.firebase.analytics.ON_ADVENTURE_GAME
import com.now.naaga.data.firebase.analytics.ON_ADVENTURE_SHOW_GIVE_UP
import com.now.naaga.data.firebase.analytics.ON_ADVENTURE_SHOW_HINT
import com.now.naaga.data.firebase.analytics.ON_ADVENTURE_SHOW_POLAROID
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.databinding.ActivityOnAdventureBinding
import com.now.naaga.presentation.adventureresult.AdventureResultActivity
import com.now.naaga.presentation.common.dialog.LetterReadDialog
import com.now.naaga.presentation.common.dialog.NaagaAlertDialog
import com.now.naaga.presentation.common.dialog.PolaroidDialog
import com.now.naaga.presentation.uimodel.mapper.toDomain
import com.now.naaga.presentation.uimodel.mapper.toUi
import com.now.naaga.presentation.uimodel.model.AdventureUiModel
import com.now.naaga.util.extension.getParcelableCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnAdventureActivity :
    AppCompatActivity(),
    NaverMapSettingDelegate by DefaultNaverMapSettingDelegate(),
    AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: ActivityOnAdventureBinding
    private val viewModel: OnAdventureViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setNaverMap(this, R.id.fcv_onAdventure_map)
        super.onCreate(savedInstanceState)
        binding = ActivityOnAdventureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerAnalytics(this.lifecycle)
        initViewModel()
        subscribe()
        setOnMapReady { setLocationChangeListener() }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        setClickListeners()
    }

    private var backPressedTime = 0L
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (System.currentTimeMillis() - backPressedTime <= 2000) {
                finish()
            } else {
                backPressedTime = System.currentTimeMillis()
                Toast.makeText(
                    this@OnAdventureActivity,
                    getString(R.string.OnAdventure_warning_back_pressed),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    private fun setClickListeners() {
        binding.clOnAdventureStop.setOnClickListener {
            logClickEvent(getViewEntryName(it), ON_ADVENTURE_SHOW_GIVE_UP)
            showGiveUpDialog()
        }
        binding.clOnAdventureShowPhoto.setOnClickListener {
            logClickEvent(getViewEntryName(it), ON_ADVENTURE_SHOW_POLAROID)
            showPolaroidDialog()
        }
        binding.clOnAdventureSearchDirection.setOnClickListener {
            logClickEvent(getViewEntryName(it), ON_ADVENTURE_SHOW_HINT)
            showHintDialog()
        }
        binding.btnOnAdventureArrived.setOnClickListener {
            logClickEvent(getViewEntryName(it), ON_ADVENTURE_END_ADVENTURE)
            viewModel.endAdventure()
        }
    }

    private fun subscribe() {
        viewModel.startCoordinate.observe(this) {
            beginAdventure(it)
        }
        viewModel.adventure.observe(this) {
            isAdventureDone(it.adventureStatus)
        }
        viewModel.hints.observe(this) { hints ->
            drawHintMarkers(hints)
            binding.lottieOnAdventureLoading.visibility = View.GONE
            showPolaroidDialog()
        }
        viewModel.lastHint.observe(this) {
            drawHintMarkers(listOf(it))
        }
        viewModel.remainingHintCount.observe(this) {
            // binding.tvOnAdventureHintCount.text = it.toString()
        }
        viewModel.letters.observe(this) {
            drawLetters(it)
        }
        viewModel.letter.observe(this) {
            showLetterReadDialog(it.message)
        }

        viewModel.error.observe(this) { error: DataThrowable ->
            logServerError(ON_ADVENTURE_GAME, error.code, error.message.toString())
            when (error.code) {
                OnAdventureViewModel.NO_DESTINATION -> {
                    shortToast(error.message ?: return@observe)
                    finish()
                }

                OnAdventureViewModel.NOT_ARRIVED -> {
                    val remainingTryCount: Int = viewModel.adventure.value?.remainingTryCount?.toInt() ?: 0
                    shortSnackbar(getString(R.string.onAdventure_retry, remainingTryCount))
                }

                OnAdventureViewModel.TRY_COUNT_OVER -> shortToast(getString(R.string.onAdventure_try_count_over))
                else -> shortSnackbar(error.message ?: return@observe)
            }
        }
    }

    private fun initViewModel() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun setLocationChangeListener() {
        naverMap.addOnLocationChangeListener { location ->
            val coordinate = Coordinate(location.latitude, location.longitude)
            viewModel.myCoordinate.value = coordinate
        }
    }

    private fun beginAdventure(coordinate: Coordinate) {
        val existingAdventure = intent.getParcelableCompat(ADVENTURE, AdventureUiModel::class.java)?.toDomain()

        if (existingAdventure == null) {
            viewModel.beginAdventure(coordinate)
            return
        }

        Toast.makeText(this, getString(R.string.OnAdventure_continue_adventure), Toast.LENGTH_SHORT).show()
        viewModel.setAdventure(existingAdventure)
    }

    private fun isAdventureDone(status: AdventureStatus) {
        if (status == AdventureStatus.DONE) {
            val adventureId: Long = viewModel.adventure.value?.id ?: throw IllegalStateException()
            val intent = AdventureResultActivity.getIntentWithGameId(this, adventureId)
            startActivity(intent)
            finish()
        }
    }

    private fun drawHintMarkers(hints: List<Hint>) {
        hints.forEach { hint ->
            addHintMarker(hint)
        }
    }

    private fun drawLetters(letters: List<ClosedLetter>) {
        removeLetters()
        letters.forEach { letter ->
            addLetter(letter, viewModel::getLetter)
        }
    }

    private fun showGiveUpDialog() {
        val fragment: Fragment? = supportFragmentManager.findFragmentByTag(GIVE_UP)
        if (fragment == null) {
            NaagaAlertDialog.Builder().build(
                title = getString(R.string.give_up_dialog_title),
                description = getString(R.string.give_up_dialog_description),
                positiveText = getString(R.string.give_up_dialog_continue),
                negativeText = getString(R.string.give_up_dialog_give_up),
                positiveAction = {},
                negativeAction = { viewModel.giveUpAdventure() },
            ).show(supportFragmentManager, GIVE_UP)
        } else {
            (fragment as DialogFragment).dialog?.show()
        }
    }

    private fun showHintDialog() {
        NaagaAlertDialog.Builder().build(
            title = getString(R.string.hint_using_dialog_title),
            description = getString(R.string.hint_using_dialog_description, viewModel.remainingHintCount.value),
            positiveText = getString(R.string.hint_using_dialog_continue),
            negativeText = getString(R.string.hint_using_dialog_give_up),
            positiveAction = { viewModel.openHint() },
            negativeAction = {},
        ).show(supportFragmentManager, HINT)
    }

    private fun showPolaroidDialog() {
        val image = viewModel.adventure.value?.destination?.image ?: return
        val fragment: Fragment? = supportFragmentManager.findFragmentByTag(DESTINATION_PHOTO)
        if (fragment == null) {
            PolaroidDialog.makeDialog(image).show(supportFragmentManager, DESTINATION_PHOTO)
        } else {
            (fragment as DialogFragment).dialog?.show()
        }
    }

    private fun showLetterReadDialog(content: String) {
        LetterReadDialog(content).show(supportFragmentManager, LETTER)
    }

    private fun shortSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun shortToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val DESTINATION_PHOTO = "DESTINATION_PHOTO"
        private const val GIVE_UP = "GIVE_UP"
        private const val ADVENTURE = "ADVENTURE"
        private const val HINT = "HINT"
        private const val LETTER = "LETTER"

        fun getIntent(context: Context): Intent {
            return Intent(context, OnAdventureActivity::class.java)
        }

        fun getIntentWithAdventure(context: Context, adventure: Adventure): Intent {
            return Intent(context, OnAdventureActivity::class.java).apply {
                putExtra(ADVENTURE, adventure.toUi())
            }
        }
    }
}
