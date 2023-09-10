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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.now.domain.model.Coordinate
import com.now.naaga.R
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.firebase.analytics.UPLOAD_OPEN_CAMERA
import com.now.naaga.data.firebase.analytics.UPLOAD_SET_COORDINATE
import com.now.naaga.data.repository.DefaultPlaceRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.databinding.ActivityUploadBinding
import com.now.naaga.presentation.beginadventure.LocationPermissionDialog
import com.now.naaga.presentation.beginadventure.LocationPermissionDialog.Companion.TAG_LOCATION_DIALOG
import com.now.naaga.presentation.upload.CameraPermissionDialog.Companion.TAG_CAMERA_DIALOG

class UploadActivity : AppCompatActivity(), AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var viewModel: UploadViewModel

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

        initViewModel()
        subscribe()
        registerAnalytics(this.lifecycle)
        requestPermission()
        setCoordinate()
        setClickListeners()
    }

    private fun initViewModel() {
        val repository = DefaultPlaceRepository()
        val factory = UploadFactory(application, repository)
        viewModel = ViewModelProvider(this, factory)[UploadViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setClickListeners()
    }

    private fun subscribe() {
        viewModel.successUpload.observe(this) { isSuccess ->
            if (isSuccess) {
                finish()
            }
        }
        viewModel.throwable.observe(this) { error: DataThrowable ->
            when (error.code) {
                UploadViewModel.ERROR_STORE_PHOTO -> {
                    shortToast(getString(R.string.upload_error_store_photo_message))
                }

                UploadViewModel.ALREADY_EXISTS_NEARBY -> {
                    shortToast(getString(R.string.upload_error_already_exists_nearby_message))
                }

                UploadViewModel.ERROR_POST_BODY -> {
                    shortToast(getString(R.string.upload_error_post_message))
                }
            }
        }
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
                val coordinate = getCoordinate(location)
                binding.tvUploadPhotoCoordinate.text = coordinate.toText()
                viewModel.setCoordinate(coordinate)
            }
        }
    }

    private fun getCoordinate(location: Location): Coordinate {
        val latitude = roundToFourDecimalPlaces(location.latitude)
        val longitude = roundToFourDecimalPlaces(location.longitude)

        return Coordinate(latitude, longitude)
    }

    private fun roundToFourDecimalPlaces(number: Double): Double {
        return (number * 10_000).toLong().toDouble() / 10_000
    }

    private fun setClickListeners() {
        binding.ivUploadCameraIcon.setOnClickListener {
            logClickEvent(getViewEntryName(it), UPLOAD_OPEN_CAMERA)
            checkCameraPermission()
        }

        binding.ivUploadPhoto.setOnClickListener {
            logClickEvent(getViewEntryName(it), UPLOAD_OPEN_CAMERA)
            checkCameraPermission()
        }
        binding.ivUploadPhotoCoordinate.setOnClickListener {
            logClickEvent(getViewEntryName(it), UPLOAD_SET_COORDINATE)
            checkLocationPermission()
        }
        binding.ivUploadClose.setOnClickListener {
            finish()
        }
        binding.btnUploadSubmit.setOnClickListener {
            if (isFormValid().not()) {
                shortToast(getString(R.string.upload_error_insufficient_info_message))
            } else {
                viewModel.postPlace()
            }
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
        val uri = getImageUri(bitmap) ?: Uri.EMPTY
        viewModel.setUri(uri)
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

    private fun shortToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isFormValid(): Boolean {
        return (isEmptyPhoto() || isEmptyTitle() || isEmptyCoordinate() || isEmptyDescription()).not()
    }

    private fun isEmptyPhoto(): Boolean {
        return viewModel.hasUri().not()
    }

    private fun isEmptyTitle(): Boolean {
        return viewModel.title.value == null
    }

    private fun isEmptyCoordinate(): Boolean {
        return viewModel.hasCoordinate().not()
    }

    private fun isEmptyDescription(): Boolean {
        return viewModel.description.value == null
    }

    private fun Coordinate.toText(): String {
        return "$latitude, $longitude"
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
