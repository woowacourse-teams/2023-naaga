package com.now.naaga.presentation.common.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.now.naaga.databinding.DialogReadLetterBinding
import com.now.naaga.presentation.uimodel.model.LetterUiModel
import com.now.naaga.util.dpToPx
import com.now.naaga.util.getWidthProportionalToDevice
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LetterReadDialog(private val content: LetterUiModel) : DialogFragment() {
    private lateinit var binding: DialogReadLetterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogReadLetterBinding.inflate(layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContent()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(true)
        setSize()
    }

    private fun setSize() {
        val dialogWidth = getWidthProportionalToDevice(requireContext(), WIDTH_RATE)
        val dialogHeight = dpToPx(requireContext(), HEIGHT)
        dialog?.window?.setLayout(dialogWidth, dialogHeight)
    }

    private fun setContent() {
        binding.letter = content
        binding.tvDialogLetterDate.text = getFormattedDate()
    }

    private fun getFormattedDate(): String {
        val serverLocalDateTime = LocalDateTime.parse(content.registerDate, DateTimeFormatter.ISO_DATE_TIME)
        val outputFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN)
        return serverLocalDateTime.format(outputFormatter)
    }

    companion object {
        private const val WIDTH_RATE = 0.78f
        private const val HEIGHT = 430
        private const val DATE_FORMAT_PATTERN = "yyyy-MM-dd"
    }
}
