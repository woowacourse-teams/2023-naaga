package com.now.naaga.presentation.upload

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.material.snackbar.Snackbar
import com.now.domain.model.Coordinate
import com.now.naaga.R
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.firebase.analytics.UPLOAD_OPEN_CAMERA
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.databinding.ActivityUploadBinding
import com.now.naaga.util.BitmapBuilder
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
    private var imageUri: Uri? = null
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (!success) return@registerForActivityResult
        if (imageUri == null) {
            showToast(getString(R.string.upload_image_orientation_error))
            return@registerForActivityResult
        }
        setImage(requireNotNull(imageUri) { "imageUri가 null입니다" })
    }

    private val storagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGranted: Boolean = permissions.values.all { it }
            if (!isGranted) {
                showStoragePermissionSnackBar()
                return@registerForActivityResult
            }
            openCamera()
        }

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGranted: Boolean = permissions.values.all { it }
            if (!isGranted) {
                showLocationPermissionSnackBar()
                return@registerForActivityResult
            }
            setCoordinate()
        }

    private val isStoragePermissionGranted: Boolean
        get() = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            storagePermissionsBelow28.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
        } else {
            true
        }

    private val locationSettingLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        setCoordinate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        subscribe()
        setClickListeners()
        locationPermissionLauncher.launch(locationPermissions)
        registerAnalytics(this.lifecycle)
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

    private fun setClickListeners() {
        binding.ivUploadCameraIcon.setOnClickListener {
            logClickEvent(getViewEntryName(it), UPLOAD_OPEN_CAMERA)
            openCameraWithPermission()
        }
        binding.ivUploadPhoto.setOnClickListener {
            logClickEvent(getViewEntryName(it), UPLOAD_OPEN_CAMERA)
            openCameraWithPermission()
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

    private fun changeVisibility(view: View, status: Int) {
        view.visibility = status
    }

    private fun showStoragePermissionSnackBar() {
        binding.root.showSnackbarWithEvent(
            message = getString(R.string.snackbar_storage_message),
            actionTitle = getString(R.string.snackbar_action_title),
            length = Snackbar.LENGTH_LONG,
            action = { openSetting() },
        )
    }

    private fun showLocationPermissionSnackBar() {
        binding.root.showSnackbarWithEvent(
            message = getString(R.string.snackbar_location_message),
            actionTitle = getString(R.string.snackbar_action_title),
            length = Snackbar.LENGTH_INDEFINITE,
            action = {
                val appDetailsIntent = getSettingIntent()
                locationSettingLauncher.launch(appDetailsIntent)
            },
        )
    }

    private fun getSettingIntent() = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:$packageName"),
    ).addCategory(Intent.CATEGORY_DEFAULT)

    private fun setImage(uri: Uri) {
        binding.ivUploadCameraIcon.visibility = View.GONE
        val bitmap = getBitmap(uri)
        binding.ivUploadPhoto.setImageBitmap(bitmap)
        val file = saveFile(bitmap)
        viewModel.setFile(file)
    }

    private fun getBitmap(uri: Uri) = BitmapBuilder(uri, contentResolver)
        .addScaling(RESIZE)
        .setProperRotate()
        .build()

    private fun saveFile(bitmap: Bitmap): File {
        val tempFile = File.createTempFile(System.currentTimeMillis().toString(), ".jpeg", cacheDir)
        FileOutputStream(tempFile).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        return tempFile
    }

    private fun setCoordinate() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            showLocationPermissionSnackBar()
            return
        }
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, createCancellationToken())
            .addOnSuccessListener { location ->
                location.let { viewModel.setCoordinate(getCoordinate(location)) }
            }.addOnFailureListener { }
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

    private fun openCameraWithPermission() {
        if (!isStoragePermissionGranted) {
            storagePermissionLauncher.launch(storagePermissionsBelow28)
            return
        }
        openCamera()
    }

    private fun openCamera() {
        imageUri = createImageUri().getOrElse {
            Snackbar.make(binding.root, getString(R.string.upload_retry), Snackbar.LENGTH_SHORT).show()
            return
        }
        cameraLauncher.launch(imageUri)
    }

    private fun createImageUri(): Result<Uri> {
        val imageUri: Uri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues,
        ) ?: return Result.failure(IllegalStateException("이미지 uri를 가져오지 못했습니다."))
        return Result.success(imageUri)
    }

    private fun isFormValid(): Boolean {
        return viewModel.isFormValid()
    }

    companion object {
        private const val PRIORITY_HIGH_ACCURACY = 100
        private const val RESIZE = 500
        private val storagePermissionsBelow28 = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
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

        fun getIntent(context: Context): Intent {
            return Intent(context, UploadActivity::class.java)
        }
    }
}
