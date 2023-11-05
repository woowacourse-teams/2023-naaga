package com.now.naaga.presentation.beginadventure

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.now.domain.model.AdventureStatus
import com.now.naaga.R
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.BEGIN_BEGIN_ADVENTURE
import com.now.naaga.data.firebase.analytics.BEGIN_GO_MYPAGE
import com.now.naaga.data.firebase.analytics.BEGIN_GO_SETTING
import com.now.naaga.data.firebase.analytics.BEGIN_GO_UPLOAD
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.databinding.ActivityBeginAdventureBinding
import com.now.naaga.presentation.mypage.MyPageActivity
import com.now.naaga.presentation.onadventure.OnAdventureActivity
import com.now.naaga.presentation.setting.SettingActivity
import com.now.naaga.presentation.upload.UploadActivity
import com.now.naaga.util.extension.openSetting
import com.now.naaga.util.extension.showSnackbarWithEvent
import com.now.naaga.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BeginAdventureActivity : AppCompatActivity(), AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: ActivityBeginAdventureBinding
    private val viewModel: BeginAdventureViewModel by viewModels()

    private val onAdventureActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            startLoading()
            fetchInProgressAdventure()
        }

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val isGranted = it.value
                if (isGranted.not()) {
                    showPermissionSnackbar()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeginAdventureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startLoading()
        registerAnalytics(this.lifecycle)
        fetchInProgressAdventure()
        setClickListeners()
        subscribe()

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun fetchInProgressAdventure() {
        viewModel.fetchAdventure(AdventureStatus.IN_PROGRESS)
    }

    private fun subscribe() {
        viewModel.loading.observe(this) { loading ->
            setLoadingView(loading)
        }
        viewModel.throwable.observe(this) { throwable: DataThrowable ->
            when (throwable.code) {
                DataThrowable.NETWORK_THROWABLE_CODE -> { showToast(getString(R.string.network_error_message)) }
            }
        }
    }

    private fun setLoadingView(loading: Boolean) {
        if (!loading) {
            finishLoading()
        }
    }

    private fun startLoading() {
        binding.lottieBeginAdventureLoading.visibility = View.VISIBLE
    }

    private fun finishLoading() {
        binding.lottieBeginAdventureLoading.visibility = View.GONE
    }

    private fun setClickListeners() {
        binding.btnBeginAdventureBegin.setOnClickListener {
            logClickEvent(getViewEntryName(it), BEGIN_BEGIN_ADVENTURE)
            checkPermissionAndBeginAdventure()
        }
        binding.btnBeginAdventureUpload.setOnClickListener {
            logClickEvent(getViewEntryName(it), BEGIN_GO_UPLOAD)
            startActivity(UploadActivity.getIntent(this))
        }
        binding.btnBeginAdventureMyPage.setOnClickListener {
            logClickEvent(getViewEntryName(it), BEGIN_GO_MYPAGE)
            startActivity(MyPageActivity.getIntent(this))
        }
        binding.ivBeginAdventureSetting.setOnClickListener {
            logClickEvent(getViewEntryName(it), BEGIN_GO_SETTING)
            startActivity(SettingActivity.getIntent(this))
        }
    }

    private fun showPermissionSnackbar() {
        binding.root.showSnackbarWithEvent(
            message = getString(R.string.snackbar_location_message),
            actionTitle = getString(R.string.snackbar_action_title),
            action = { openSetting() },
        )
    }

    private fun checkPermissionAndBeginAdventure() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            locationPermissionLauncher.launch(locationPermissions)
        } else {
            checkLocationPermissionInStatusBar()
        }
    }

    private fun checkLocationPermissionInStatusBar() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            Toast.makeText(this, GPS_TURN_ON_MESSAGE, Toast.LENGTH_SHORT).show()
        } else {
            onAdventureActivityLauncher.launch(getIntentWithAdventureOrWithout())
        }
    }

    private fun getIntentWithAdventureOrWithout(): Intent {
        val existingAdventure = viewModel.adventure.value

        return if (existingAdventure == null) {
            OnAdventureActivity.getIntent(this)
        } else {
            OnAdventureActivity.getIntentWithAdventure(this, existingAdventure)
        }
    }

    companion object {
        private val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )

        private const val GPS_TURN_ON_MESSAGE = "GPS 설정을 켜주세요"

        fun getIntent(context: Context): Intent {
            return Intent(context, BeginAdventureActivity::class.java)
        }
    }
}
