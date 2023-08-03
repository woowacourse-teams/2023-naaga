package com.now.naaga.presentation.beginadventure

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.now.naaga.R
import com.now.naaga.databinding.ActivityBeginAdventureBinding
import com.now.naaga.presentation.beginadventure.LocationPermissionDialog.Companion.TAG_LOCATION_DIALOG
import com.now.naaga.presentation.onadventure.OnAdventureActivity
import com.now.naaga.presentation.rank.RankActivity

class BeginAdventureActivity : AppCompatActivity() {
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
            checkPermissionAndBeginAdventure()
        }

        binding.ivBeginAdventureRank.setOnClickListener {
            startActivity(RankActivity.getIntent(this))
        }
    }

    private fun checkPermissionAndBeginAdventure() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            LocationPermissionDialog().show(supportFragmentManager, TAG_LOCATION_DIALOG)
        } else {
            startActivity(OnAdventureActivity.getIntent(this))
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, BeginAdventureActivity::class.java)
        }
    }
}
