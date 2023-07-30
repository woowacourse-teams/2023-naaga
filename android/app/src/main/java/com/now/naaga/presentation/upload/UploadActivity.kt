package com.now.naaga.presentation.upload

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.now.naaga.databinding.ActivityUploadBinding
import com.now.naaga.presentation.upload.CameraPermissionDialog.Companion.TAG_CAMERA_DIALOG

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()
        getCoordinate()

        binding.btnUploadSubmit.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                CameraPermissionDialog().show(supportFragmentManager, TAG_CAMERA_DIALOG)
            }
        }
    }

    private fun getCoordinate() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if (location != null) {
                val latitude = roundToFourDecimalPlaces(location.latitude)
                val longitude = roundToFourDecimalPlaces(location.longitude)

                val coordinate = "$latitude, $longitude"
                binding.tvUploadPhotoCoordinate.text = coordinate
            }
        }
    }

    private fun roundToFourDecimalPlaces(number: Double): Double {
        return (number * 10000).toLong().toDouble() / 10000
    }

    private fun checkPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA)
        }
    }

    companion object {
        private const val REQUEST_CODE_CAMERA = 1000

        fun getIntent(context: Context): Intent {
            return Intent(context, UploadActivity::class.java)
        }
    }
}
