package com.now.naaga.presentation.beginadventure

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment
import com.now.naaga.databinding.DialogDestinationPhotoBinding
import com.now.naaga.util.getWidthProportionalToDevice

class DestinationPhotoDialog : DialogFragment() {
    private lateinit var binding: DialogDestinationPhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogDestinationPhotoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false)
        clickClose()
        setSize()
    }

    private fun clickClose() {
        binding.ivImageDialogClose.setOnClickListener {
            dialog?.hide()
        }
    }

    private fun setSize() {
        val dialogWidth = getWidthProportionalToDevice(requireContext(), 0.9f)
        dialog?.window?.setLayout(dialogWidth, WRAP_CONTENT)
    }
}
