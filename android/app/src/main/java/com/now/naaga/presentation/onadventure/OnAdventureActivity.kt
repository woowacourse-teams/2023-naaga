package com.now.naaga.presentation.onadventure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.now.domain.model.Adventure
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Coordinate
import com.now.domain.model.Hint
import com.now.naaga.R
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.firebase.analytics.ON_ADVENTURE_END_ADVENTURE
import com.now.naaga.data.firebase.analytics.ON_ADVENTURE_SHOW_GIVE_UP
import com.now.naaga.data.firebase.analytics.ON_ADVENTURE_SHOW_HINT
import com.now.naaga.data.firebase.analytics.ON_ADVENTURE_SHOW_POLAROID
import com.now.naaga.databinding.ActivityOnAdventureBinding
import com.now.naaga.presentation.adventureresult.AdventureResultActivity
import com.now.naaga.presentation.uimodel.mapper.toDomain
import com.now.naaga.presentation.uimodel.mapper.toUi
import com.now.naaga.presentation.uimodel.model.AdventureUiModel
import com.now.naaga.util.getParcelable

class OnAdventureActivity :
    AppCompatActivity(),
    NaverMapSettingDelegate by DefaultNaverMapSettingDelegate(),
    AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: ActivityOnAdventureBinding
    private lateinit var viewModel: OnAdventureViewModel

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

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, OnAdventureViewModel.Factory)[OnAdventureViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private var backPressedTime = 0L
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (System.currentTimeMillis() - backPressedTime <= 2000) {
                finish()
            } else {
                backPressedTime = System.currentTimeMillis()
                Toast.makeText(this@OnAdventureActivity, "뒤로가기 버튼을 한번 더 누르면 게임에서 나가져요!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setClickListeners() {
        binding.ivOnAdventureGiveUp.setOnClickListener {
            logClickEvent(getViewEntryName(it), ON_ADVENTURE_SHOW_GIVE_UP)
            showGiveUpDialog()
        }
        binding.ivOnAdventurePhoto.setOnClickListener {
            logClickEvent(getViewEntryName(it), ON_ADVENTURE_SHOW_POLAROID)
            showPolaroidDialog()
        }
        binding.ivOnAdventureHint.setOnClickListener {
            logClickEvent(getViewEntryName(it), ON_ADVENTURE_SHOW_HINT)
            showHintDialog()
        }
        binding.btnOnAdventureArrived.setOnClickListener {
            logClickEvent(getViewEntryName(it), ON_ADVENTURE_END_ADVENTURE)
            viewModel.endAdventure()
        }
    }

    private fun setLocationChangeListener() {
        naverMap.addOnLocationChangeListener { location ->
            val coordinate = Coordinate(location.latitude, location.longitude)
            viewModel.calculateDistance(coordinate)
            viewModel.myCoordinate.value = coordinate
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
        }
        viewModel.lastHint.observe(this) {
            drawHintMarkers(listOf(it))
        }
        viewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun beginAdventure(coordinate: Coordinate) {
        val existingAdventure = intent.getParcelable(ADVENTURE, AdventureUiModel::class.java)?.toDomain()

        if (existingAdventure == null) {
            viewModel.beginAdventure(coordinate)
            return
        }

        Toast.makeText(this, "진행중인 게임에 입장했습니다.", Toast.LENGTH_SHORT).show()
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
        val remainingHintCount: Int =
            OnAdventureViewModel.MAX_HINT_COUNT - (viewModel.adventure.value?.hints?.size ?: 0)
        NaagaAlertDialog.Builder().build(
            title = getString(R.string.hint_using_dialog_title),
            description = getString(R.string.hint_using_dialog_description, remainingHintCount),
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

    companion object {
        private const val DESTINATION_PHOTO = "DESTINATION_PHOTO"
        private const val GIVE_UP = "GIVE_UP"
        private const val ADVENTURE = "ADVENTURE"
        private const val HINT = "HINT"

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
