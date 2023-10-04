package com.now.naaga.common.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.now.naaga.databinding.DialogConfirmBinding
import com.now.naaga.util.getWidthProportionalToDevice

class ConfirmDialog private constructor() : DialogFragment() {
    private var _binding: DialogConfirmBinding? = null
    private val binding: DialogConfirmBinding
        get() = requireNotNull(_binding) { BINDING_NULL_ERROR }
    private var title: String? = null
    private var description: String? = null
    private var positiveText: String? = null
    private var negativeText: String? = null
    private lateinit var positiveAction: () -> Unit
    private lateinit var negativeAction: () -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogConfirmBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSize()
        setBackgroundTransparent()
        setTitle()
        setDescription()
        setPositiveText()
        setNegativeText()
        binding.tvConfirmDialogPositive.setOnClickListener { positiveAction(); dismiss() }
        binding.tvConfirmDialogNegative.setOnClickListener { negativeAction(); dismiss() }
        isCancelable = false
    }

    private fun setSize() {
        val dialogWidth = getWidthProportionalToDevice(requireContext(), WIDTH_RATE)
        dialog?.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setBackgroundTransparent() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setTitle() {
        binding.tvConfirmDialogTitle.text = title
    }

    private fun setDescription() {
        binding.tvConfirmDialogDescription.text = description
    }

    private fun setPositiveText() {
        binding.tvConfirmDialogPositive.text = positiveText
    }

    private fun setNegativeText() {
        binding.tvConfirmDialogNegative.text = negativeText
    }

    class Builder {
        fun build(
            title: String,
            description: String,
            positiveText: String,
            negativeText: String,
            positiveAction: () -> Unit,
            negativeAction: () -> Unit,
        ): ConfirmDialog {
            return ConfirmDialog().apply {
                this.title = title
                this.description = description
                this.positiveText = positiveText
                this.negativeText = negativeText
                this.positiveAction = positiveAction
                this.negativeAction = negativeAction
            }
        }
    }

    companion object {
        private const val WIDTH_RATE = 0.9f
        private const val BINDING_NULL_ERROR = "ConfirmDialog에서 바인딩 초기화 에러가 발생했습니다."
    }
}
