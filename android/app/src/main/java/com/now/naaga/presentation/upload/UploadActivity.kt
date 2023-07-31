package com.now.naaga.presentation.upload

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.now.naaga.databinding.ActivityUploadBinding
import com.now.naaga.presentation.upload.CameraPermissionDialog.Companion.TAG_CAMERA_DIALOG

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview(),
    ) { bitmap ->
        if (bitmap != null) {
            setImage(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()
        setCoordinate()
        bindListener()
    }

    private fun bindListener() {
        binding.ivUploadCameraIcon.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                CameraPermissionDialog().show(supportFragmentManager, TAG_CAMERA_DIALOG)
            } else {
                cameraLauncher.launch(null)
            }
        }

        binding.btnUploadSubmit.setOnClickListener {
        }
    }

    private fun setImage(bitmap: Bitmap?) {
        binding.ivUploadCameraIcon.visibility = View.GONE
        binding.ivUploadPhoto.setImageBitmap(bitmap)
    }

    private fun setCoordinate() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if (location != null) {
                binding.tvUploadPhotoCoordinate.text = getCoordinate(location)
            }
        }
    }

    private fun getCoordinate(location: Location): String {
        val latitude = roundToFourDecimalPlaces(location.latitude)
        val longitude = roundToFourDecimalPlaces(location.longitude)

        return "$latitude, $longitude"
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
