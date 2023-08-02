package com.now.naaga.presentation.upload

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.now.naaga.databinding.ActivityUploadBinding
import com.now.naaga.presentation.beginadventure.LocationPermissionDialog
import com.now.naaga.presentation.beginadventure.LocationPermissionDialog.Companion.TAG_LOCATION_DIALOG
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permission: Map<String, Boolean> ->
        permission.entries.forEach { entry ->
            val isGranted = entry.value
            if (isGranted.not()) {
                when (entry.key) {
                    Manifest.permission.CAMERA -> {
                        CameraPermissionDialog().show(supportFragmentManager, TAG_CAMERA_DIALOG)
                    }
                    Manifest.permission.ACCESS_FINE_LOCATION -> {
                        LocationPermissionDialog().show(supportFragmentManager, TAG_LOCATION_DIALOG)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermission()
        setCoordinate()
        bindListener()
    }

    private fun requestPermission() {
        val permissionsToRequest = mutableListOf<String>()
        requestPermissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }
        requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
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
        return (number * 10_000).toLong().toDouble() / 10_000
    }

    private fun bindListener() {
        binding.ivUploadCameraIcon.setOnClickListener {
            checkCameraPermission()
        }

        binding.ivUploadPhoto.setOnClickListener {
            checkCameraPermission()
        }
        binding.ivUploadPhotoCoordinate.setOnClickListener {
            checkLocationPermission()
        }
        binding.ivUploadClose.setOnClickListener {
            finish()
        }
    }

    private fun checkCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            CameraPermissionDialog().show(supportFragmentManager, TAG_CAMERA_DIALOG)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        cameraLauncher.launch(null)
    }

    private fun checkLocationPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            LocationPermissionDialog().show(supportFragmentManager, TAG_LOCATION_DIALOG)
        } else {
            setCoordinate()
        }
    }

    private fun setImage(bitmap: Bitmap) {
        binding.ivUploadCameraIcon.visibility = View.GONE
        binding.ivUploadPhoto.setImageBitmap(bitmap)
    }

    private fun getImageUri(bitmap: Bitmap): Uri? {
        val resolver = applicationContext.contentResolver
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?.let { imageUri ->
                val outputStream = resolver.openOutputStream(imageUri)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                }
                return imageUri
            }
        return null
    }

    companion object {
        private val requestPermissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
        )

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "ImageTitle")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        fun getIntent(context: Context): Intent {
            return Intent(context, UploadActivity::class.java)
        }
    }
}
