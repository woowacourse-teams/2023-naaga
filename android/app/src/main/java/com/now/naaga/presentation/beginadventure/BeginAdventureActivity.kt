package com.now.naaga.presentation.beginadventure

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.now.naaga.databinding.ActivityBeginAdventureBinding
import com.now.naaga.presentation.beginadventure.LocationPermissionDialog.Companion.TAG_LOCATION_DIALOG
import com.now.naaga.presentation.onadventure.OnAdventureActivity

class BeginAdventureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBeginAdventureBinding

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // precise location access granted
                    Toast.makeText(this, "위치 권한 요청이 허용되었습니다", Toast.LENGTH_SHORT).show()
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // only approximate location access granted.
                    Toast.makeText(this, "대략적인 위치 권한만 허용되었습니다", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    // all kind of access denied
                    Toast.makeText(this, "위치 권한 요청이 거절되었습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeginAdventureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestLocationPermission()
        setClickListeners()
    }

    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
        )
    }

    private fun setClickListeners() {
        binding.clBeginAdventureBegin.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                LocationPermissionDialog().show(supportFragmentManager, TAG_LOCATION_DIALOG)
            } else {
                startActivity(OnAdventureActivity.getIntent(this))
            }
        }
        checkPermissionAndBeginAdventure()
    }

    private fun checkPermissionAndBeginAdventure() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            LocationPermissionDialog().show(supportFragmentManager, TAG_LOCATION_DIALOG)
        } else {
            startActivity(Intent(this, OnAdventureActivity::class.java))
        }
    }
}
