package com.now.naaga.presentation.upload

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.now.domain.model.Coordinate
import com.now.naaga.R
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.firebase.analytics.UPLOAD_OPEN_CAMERA
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.databinding.ActivityUploadBinding
import com.now.naaga.presentation.upload.UploadViewModel.Companion.FILE_EMPTY
import com.now.naaga.util.extension.openSetting
import com.now.naaga.util.extension.showSnackbarWithEvent
import com.now.naaga.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

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

        val keys = permission.entries.map { it.key }
        val isStorageRequest = storagePermissions.any { keys.contains(it) }
        if (isStorageRequest) {
            if (permission.entries.map { it.value }.contains(false)) {
                showPermissionSnackbar(getString(R.string.snackbar_storage_message))
            } else {
                openCamera()
            }
            return@registerForActivityResult
        }
        showPermissionSnackbar(getString(R.string.snackbar_location_message))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        subscribe()
        registerAnalytics(this.lifecycle)
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
                    showToast(getString(R.string.upload_success_submit))
                    finish()
                }

                UploadStatus.PENDING -> {
                    changeVisibility(binding.lottieUploadLoading, View.VISIBLE)
                }

                UploadStatus.FAIL -> {
                    changeVisibility(binding.lottieUploadLoading, View.GONE)
                    showToast(getString(R.string.upload_fail_submit))
                }
            }
        }
        viewModel.throwable.observe(this) { throwable: DataThrowable ->
            when (throwable.code) {
                UploadViewModel.ERROR_STORE_PHOTO -> showToast(getString(R.string.upload_error_store_photo_message))
                UploadViewModel.ERROR_POST_BODY -> showToast(getString(R.string.upload_error_post_message))
                DataThrowable.NETWORK_THROWABLE_CODE -> showToast(getString(R.string.network_error_message))
            }
        }
    }

    private fun changeVisibility(view: View, status: Int) {
        view.visibility = status
    }

    private fun showPermissionSnackbar(message: String) {
        binding.root.showSnackbarWithEvent(
            message = message,
            actionTitle = getString(R.string.snackbar_action_title),
            action = { openSetting() },
        )
    }

    private fun setCoordinate() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, createCancellationToken())
                .addOnSuccessListener { location ->
                    location.let { viewModel.setCoordinate(getCoordinate(location)) }
                }
                .addOnFailureListener { }
        } else {
            requestPermissionLauncher.launch(locationPermissions)
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
            requestStoragePermission()
        }
        binding.ivUploadPhoto.setOnClickListener {
            logClickEvent(getViewEntryName(it), UPLOAD_OPEN_CAMERA)
            requestStoragePermission()
        }
        binding.ivUploadBack.setOnClickListener {
            finish()
        }
        binding.btnUploadSubmit.setOnClickListener {
            if (isFormValid().not()) {
                showToast(getString(R.string.upload_error_insufficient_info_message))
            } else {
                viewModel.postPlace()
            }
        }
    }

    private fun requestStoragePermission() {
        val permissionToRequest = storagePermissions.toMutableList()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return openCamera()
        }

        requestPermissionLauncher.launch(permissionToRequest.toTypedArray())
    }

    private fun openCamera() {
        cameraLauncher.launch(null)
    }

    private fun setImage(bitmap: Bitmap) {
        binding.ivUploadCameraIcon.visibility = View.GONE
        binding.ivUploadPhoto.setImageBitmap(bitmap)
        val uri = getImageUri(bitmap) ?: Uri.EMPTY
        val file = makeImageFile(uri)
        viewModel.setFile(file)
    }

    private fun makeImageFile(uri: Uri): File {
        val bitmap = contentResolver.openInputStream(uri).use {
            BitmapFactory.decodeStream(it)
        }
        val tempFile = File.createTempFile("image", ".jpeg", cacheDir) ?: FILE_EMPTY

        FileOutputStream(tempFile).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        return tempFile
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

    private fun isFormValid(): Boolean {
        return viewModel.isFormValid()
    }

    companion object {
        private val storagePermissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )

        private val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "ImageTitle ${LocalDateTime.now()}")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        const val PRIORITY_HIGH_ACCURACY = 100

        fun getIntent(context: Context): Intent {
            return Intent(context, UploadActivity::class.java)
        }
    }
}
