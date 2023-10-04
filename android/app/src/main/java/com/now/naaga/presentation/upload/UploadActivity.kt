package com.now.naaga.presentation.upload

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.now.domain.model.Coordinate
import com.now.naaga.R
import com.now.naaga.common.dialog.DialogType
import com.now.naaga.common.dialog.PermissionDialog
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.firebase.analytics.UPLOAD_OPEN_CAMERA
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.databinding.ActivityUploadBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UploadActivity : AppCompatActivity(), AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: ActivityUploadBinding
    private val viewModel: UploadViewModel by viewModels()

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
                        PermissionDialog(DialogType.CAMERA).show(supportFragmentManager)
//                        CameraPermissionDialog().show(supportFragmentManager, TAG_CAMERA_DIALOG)
                    }

                    Manifest.permission.ACCESS_FINE_LOCATION -> {
                        PermissionDialog(DialogType.LOCATION).show(supportFragmentManager)
//                        LocationPermissionDialog().show(supportFragmentManager, TAG_LOCATION_DIALOG)
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
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun subscribe() {
        viewModel.coordinate.observe(this) {
            binding.lottieUploadLoading.visibility = View.GONE
        }
        viewModel.successUpload.observe(this) { uploadStatus ->
            when (uploadStatus) {
                UploadStatus.SUCCESS -> {
                    changeVisibility(binding.lottieUploadLoading, View.GONE)
                    finish()
                }
                UploadStatus.PENDING -> { changeVisibility(binding.lottieUploadLoading, View.VISIBLE) }
                UploadStatus.FAIL -> {
                    changeVisibility(binding.lottieUploadLoading, View.GONE)
                    shortToast(getString(R.string.upload_fail_submit))
                }
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
                UploadViewModel.ERROR_POST_BODY -> { shortToast(getString(R.string.upload_error_post_message)) }
            }
        }
    }

    private fun changeVisibility(view: View, status: Int) {
        when (status) {
            View.VISIBLE -> { view.visibility = status }
            View.GONE -> { view.visibility = status }
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
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, createCancellationToken())
                .addOnSuccessListener { location ->
                    location.let { viewModel.setCoordinate(getCoordinate(location)) }
                }
                .addOnFailureListener { }
        }
    }

    private fun createCancellationToken(): CancellationToken {
        return object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                return CancellationTokenSource().token
            }

            override fun isCancellationRequested(): Boolean {
                return false
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
        binding.ivUploadBack.setOnClickListener {
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
            PermissionDialog(DialogType.CAMERA).show(supportFragmentManager)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        cameraLauncher.launch(null)
    }

    private fun setImage(bitmap: Bitmap) {
        binding.ivUploadCameraIcon.visibility = View.GONE
        binding.ivUploadPhoto.setImageBitmap(bitmap)
        val uri = getAbsolutePathFromUri(getImageUri(bitmap) ?: Uri.EMPTY) ?: ""
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

    private fun getAbsolutePathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = applicationContext.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (it.moveToFirst()) {
                return it.getString(columnIndex)
            }
        }
        return null
    }

    private fun shortToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isFormValid(): Boolean {
        return viewModel.isFormValid()
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

        const val PRIORITY_HIGH_ACCURACY = 100

        fun getIntent(context: Context): Intent {
            return Intent(context, UploadActivity::class.java)
        }
    }
}
