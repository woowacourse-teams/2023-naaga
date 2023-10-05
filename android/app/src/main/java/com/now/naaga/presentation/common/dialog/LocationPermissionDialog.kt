package com.now.naaga.presentation.common.dialog

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.DialogFragment
import com.now.naaga.R
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.firebase.analytics.LOCATION_PERMISSION_OPEN_SETTING
import com.now.naaga.databinding.DialogLocationPermissionBinding
import com.now.naaga.util.dpToPx
import com.now.naaga.util.getWidthProportionalToDevice

class LocationPermissionDialog : DialogFragment(), AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: DialogLocationPermissionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogLocationPermissionBinding.inflate(layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerAnalytics(this.lifecycle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        setSize()
        binding.btnDialogLocationSetting.setOnClickListener {
            logClickEvent(requireContext().getViewEntryName(it), LOCATION_PERMISSION_OPEN_SETTING)
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

    private fun checkPermission() {
        if (checkSelfPermission(requireContext(), ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
            setDescription(true)
            return
        }
        setDescription(false)
    }

    private fun setDescription(isApproximateAccessGranted: Boolean) {
        val description: String = if (isApproximateAccessGranted) {
            getString(R.string.locationDialog_approximate_description)
        } else {
            getString(R.string.locationDialog_description)
        }

        binding.tvDialogLocationDescription.text = description
    }

    companion object {
        const val WIDTH_RATE = 0.83f
        const val HEIGHT = 400
        const val TAG_LOCATION_DIALOG = "LOCATION"
    }
}
