package com.now.naaga.presentation.beginadventure

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.now.naaga.databinding.DialogLocationPermissionBinding
import kotlin.math.roundToInt

class LocationDialogFragment : DialogFragment() {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentWidth()
        binding.btnDialogLocationSetting.setOnClickListener {
            goSetting()
            dismiss()
        }
    }

    private fun setFragmentWidth() {
        dialog?.window?.setLayout(getWidth(), convertDpToPx())
    }

    private fun getWidth(): Int {
        val windowManager: WindowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            (windowManager.currentWindowMetrics.bounds.width() * WIDTH_PERCENTAGE).toInt()
        } else {
            (windowManager.defaultDisplay.width * WIDTH_PERCENTAGE).toInt()
        }
    }

    private fun convertDpToPx(): Int {
        val density = requireContext().resources.displayMetrics.density
        return (DP.toFloat() * density).roundToInt()
    }

    private fun goSetting() {
        val appDetailsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${requireContext().packageName}"),
        ).addCategory(Intent.CATEGORY_DEFAULT)
        startActivity(appDetailsIntent)
    }

    companion object {
        const val WIDTH_PERCENTAGE = 0.83
        const val DP = 400
    }
}
