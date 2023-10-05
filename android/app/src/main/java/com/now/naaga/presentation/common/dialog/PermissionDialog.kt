package com.now.naaga.presentation.common.dialog

import android.annotation.SuppressLint
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
import androidx.fragment.app.FragmentManager
import com.now.naaga.R
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.CAMERA_PERMISSION_OPEN_SETTING
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.databinding.DialogPermissionBinding
import com.now.naaga.util.dpToPx
import com.now.naaga.util.getWidthProportionalToDevice

class PermissionDialog(private val type: DialogType) :
    DialogFragment(),
    AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: DialogPermissionBinding

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogPermissionBinding.inflate(layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        when (type) {
            DialogType.CAMERA -> {
                binding.ivDialogPermissionIcon.setImageDrawable(context?.getDrawable(R.drawable.ic_camera_dialog))
                binding.btnDialogPermissionSetting.text = getString(R.string.locationDialog_setting)
                binding.tvDialogPermissionDescription.text = getString(R.string.cameraDialog_description)
            }

            DialogType.LOCATION -> {
                binding.ivDialogPermissionIcon.setImageDrawable(context?.getDrawable(R.drawable.ic_location_dialog))
                binding.btnDialogPermissionSetting.text = getString(R.string.locationDialog_setting)
                binding.tvDialogPermissionDescription.text = getString(R.string.locationDialog_description)
            }
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerAnalytics(this.lifecycle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSize()
        binding.btnDialogPermissionSetting.setOnClickListener {
            logClickEvent(requireContext().getViewEntryName(it), CAMERA_PERMISSION_OPEN_SETTING)
            openSetting()
            dismiss()
        }
    }

    private fun setSize() {
        val dialogWidth = getWidthProportionalToDevice(requireContext(), WIDTH_RATE)
        val dialogHeight = dpToPx(requireContext(), HEIGHT)
        dialog?.window?.setLayout(dialogWidth, dialogHeight)
    }

    private fun openSetting() {
        val appDetailsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${requireContext().packageName}"),
        ).addCategory(Intent.CATEGORY_DEFAULT)
        startActivity(appDetailsIntent)
    }

    fun show(manager: FragmentManager) {
        show(manager, type.name)
    }

    companion object {
        const val WIDTH_RATE = 0.83f
        const val HEIGHT = 400
    }
}
