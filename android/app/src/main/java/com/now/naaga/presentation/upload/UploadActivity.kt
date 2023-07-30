package com.now.naaga.presentation.upload

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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

        binding.btnUploadSubmit.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                CameraPermissionDialog().show(supportFragmentManager, TAG_CAMERA_DIALOG)
            }
        }
    }

    private fun checkPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 1000)
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, UploadActivity::class.java)
        }
    }
}
