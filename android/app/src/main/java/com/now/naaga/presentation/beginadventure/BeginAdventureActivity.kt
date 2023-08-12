package com.now.naaga.presentation.beginadventure

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.now.naaga.R
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.BEGIN_BEGIN_ADVENTURE
import com.now.naaga.data.firebase.analytics.BEGIN_GO_MYPAGE
import com.now.naaga.data.firebase.analytics.BEGIN_GO_RANK
import com.now.naaga.data.firebase.analytics.BEGIN_GO_UPLOAD
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.databinding.ActivityBeginAdventureBinding
import com.now.naaga.presentation.beginadventure.LocationPermissionDialog.Companion.TAG_LOCATION_DIALOG
import com.now.naaga.presentation.mypage.MyPageActivity
import com.now.naaga.presentation.onadventure.OnAdventureActivity
import com.now.naaga.presentation.rank.RankActivity

class BeginAdventureActivity : AppCompatActivity(), AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: ActivityBeginAdventureBinding

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    Toast.makeText(this, getString(R.string.beginAdventure_precise_access), Toast.LENGTH_SHORT).show()
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Toast.makeText(this, getString(R.string.beginAdventure_approximate_access), Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {
                    Toast.makeText(this, getString(R.string.beginAdventure_denied_access), Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeginAdventureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerAnalytics(this.lifecycle)
        requestLocationPermission()
        setClickListeners()
    }

    private fun requestLocationPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    private fun setClickListeners() {
        binding.clBeginAdventureBegin.setOnClickListener {
            logClickEvent(getViewEntryName(it), BEGIN_BEGIN_ADVENTURE)
            checkPermissionAndBeginAdventure()
        }
        binding.ivBeginAdventureRank.setOnClickListener {
            logClickEvent(getViewEntryName(it), BEGIN_GO_RANK)
            startActivity(RankActivity.getIntent(this))
        }
        binding.ivBeginAdventureUpload.setOnClickListener {
            logClickEvent(getViewEntryName(it), BEGIN_GO_UPLOAD)
            Toast.makeText(this, getString(R.string.beginAdventure_features_not_ready), Toast.LENGTH_SHORT).show()
//            startActivity(UploadActivity.getIntent(this))
        }
        binding.ivBeginAdventureMypage.setOnClickListener {
            logClickEvent(getViewEntryName(it), BEGIN_GO_MYPAGE)
            startActivity(MyPageActivity.getIntent(this))
        }
    }

    private fun checkPermissionAndBeginAdventure() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            LocationPermissionDialog().show(supportFragmentManager, TAG_LOCATION_DIALOG)
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
            val intent = OnAdventureActivity.getIntent(this)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private const val GPS_TURN_ON_MESSAGE = "GPS 설정을 켜주세요"

        fun getIntent(context: Context): Intent {
            return Intent(context, BeginAdventureActivity::class.java)
        }
    }
}
