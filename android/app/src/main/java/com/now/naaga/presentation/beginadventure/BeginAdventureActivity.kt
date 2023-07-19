package com.now.naaga.presentation.beginadventure

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.now.naaga.databinding.ActivityBeginAdventureBinding
import com.now.naaga.presentation.onadventure.OnAdventureActivity

class BeginAdventureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBeginAdventureBinding

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBeginAdventureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        binding.btnBeginAdventureButton.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                LocationDialogFragment().show(supportFragmentManager, "location")
            } else {
                startActivity(Intent(this, OnAdventureActivity::class.java))
            }
        }
    }
}
