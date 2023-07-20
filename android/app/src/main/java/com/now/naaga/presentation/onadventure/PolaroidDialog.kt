package com.now.naaga.presentation.onadventure

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.now.naaga.databinding.DialogPolaroidBinding
import com.now.naaga.util.getWidthProportionalToDevice

class PolaroidDialog : DialogFragment() {
    private lateinit var binding: DialogPolaroidBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogPolaroidBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false)
        clickClose()
        setSize()
        setPhoto()
    }

    private fun setPhoto() {
        val photo: String? = arguments?.getString(PHOTO)
        Glide.with(binding.ivImageDialogDestinationImage)
            .load(photo)
            .into(binding.ivImageDialogDestinationImage)
    }

    private fun clickClose() {
        binding.ivImageDialogClose.setOnClickListener {
            dialog?.hide()
        }
    }

    private fun setSize() {
        val dialogWidth = getWidthProportionalToDevice(requireContext(), WIDTH_RATE)
        dialog?.window?.setLayout(dialogWidth, WRAP_CONTENT)
    }

    companion object {
        private const val WIDTH_RATE = 0.9f
        private const val PHOTO = "PHOTO"

        fun makeDialog(photo: String): PolaroidDialog {
            return PolaroidDialog().apply {
                val bundle = Bundle()
                bundle.putString(PHOTO, photo)
                arguments = bundle
            }
        }
    }
}
