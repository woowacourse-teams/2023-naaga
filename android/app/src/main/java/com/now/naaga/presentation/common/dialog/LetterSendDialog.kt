package com.now.naaga.presentation.common.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.now.naaga.databinding.DialogSendLetterBinding
import com.now.naaga.util.dpToPx
import com.now.naaga.util.getWidthProportionalToDevice

class LetterSendDialog(
    private val onClick: (String) -> Unit,
) : DialogFragment() {
    private lateinit var binding: DialogSendLetterBinding
    var message: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogSendLetterBinding.inflate(layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.dialog = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(true)
        setSize()
        setClickListener()
    }

    private fun setSize() {
        val dialogWidth = getWidthProportionalToDevice(requireContext(), WIDTH_RATE)
        val dialogHeight = dpToPx(requireContext(), HEIGHT)
        dialog?.window?.setLayout(dialogWidth, dialogHeight)
    }

    private fun setClickListener() {
        binding.btnDialogLetterSubmit.setOnClickListener { onClick(message) }
    }

    companion object {
        const val TAG = "SEND_LETTER"
        private const val WIDTH_RATE = 0.78f
        private const val HEIGHT = 430
    }
}
