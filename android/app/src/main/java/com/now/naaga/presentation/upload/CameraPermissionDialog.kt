package com.now.naaga.presentation.upload

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.now.naaga.databinding.DialogCameraPermissionBinding
import com.now.naaga.presentation.beginadventure.LocationPermissionDialog
import com.now.naaga.util.dpToPx
import com.now.naaga.util.getWidthProportionalToDevice

class CameraPermissionDialog : DialogFragment() {
    private lateinit var binding: DialogCameraPermissionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogCameraPermissionBinding.inflate(layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSize()
        binding.btnDialogLocationSetting.setOnClickListener {
            goSetting()
            dismiss()
        }
    }

    private fun setSize() {
        val dialogWidth = getWidthProportionalToDevice(requireContext(), LocationPermissionDialog.WIDTH_RATE)
        val dialogHeight = dpToPx(requireContext(), LocationPermissionDialog.HEIGHT)
        dialog?.window?.setLayout(dialogWidth, dialogHeight)
    }

    private fun goSetting() {
        val appDetailsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${requireContext().packageName}"),
        ).addCategory(Intent.CATEGORY_DEFAULT)
        startActivity(appDetailsIntent)
    }

    companion object {
        const val TAG_CAMERA_DIALOG = "CAMERA"
    }
}
