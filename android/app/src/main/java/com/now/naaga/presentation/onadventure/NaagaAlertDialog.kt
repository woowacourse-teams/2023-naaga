package com.now.naaga.presentation.onadventure

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.now.naaga.databinding.DialogNaagaAlertBinding
import com.now.naaga.util.getWidthProportionalToDevice

class NaagaAlertDialog private constructor() : DialogFragment() {
    private var _binding: DialogNaagaAlertBinding? = null
    private val binding: DialogNaagaAlertBinding get() = requireNotNull(_binding) { BINDING_NULL_ERROR }
    private var title: String? = null
    private var description: String? = null
    private var positiveText: String? = null
    private var negativeText: String? = null
    private lateinit var positiveAction: () -> Unit
    private lateinit var negativeAction: () -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogNaagaAlertBinding.inflate(layoutInflater)
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
        binding.tvAlertDialogPositive.setOnClickListener { positiveAction(); dismiss() }
        binding.tvAlertDialogNegative.setOnClickListener { negativeAction(); dismiss() }
    }

    private fun setSize() {
        val dialogWidth = getWidthProportionalToDevice(requireContext(), WIDTH_RATE)
        dialog?.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setBackgroundTransparent() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setTitle() {
        binding.tvAlertDialogTitle.text = title
    }

    private fun setDescription() {
        binding.tvAlertDialogDescription.text = description
    }

    private fun setPositiveText() {
        binding.tvAlertDialogPositive.text = positiveText
    }

    private fun setNegativeText() {
        binding.tvAlertDialogNegative.text = negativeText
    }

    fun setPositiveButton(action: () -> Unit) {
        positiveAction = action
    }

    fun setNegativeButton(action: () -> Unit) {
        negativeAction = action
    }

    class Builder() {
        private var isCancelable = true

        fun build(
            title: String,
            description: String,
            positiveText: String,
            negativeText: String,
            positiveAction: () -> Unit,
            negativeAction: () -> Unit,
        ): NaagaAlertDialog {
            return NaagaAlertDialog().apply {
                this.title = title
                this.description = description
                this.positiveText = positiveText
                this.negativeText = negativeText
                this.positiveAction = positiveAction
                this.negativeAction = negativeAction
                this.isCancelable = this@Builder.isCancelable
            }
        }

        fun setIsCancelable(isCancelable: Boolean): Builder {
            this.isCancelable = isCancelable
            return this
        }
    }

    companion object {
        private const val WIDTH_RATE = 0.9f
        private const val BINDING_NULL_ERROR = "NaagaAlertDialog에서 바인딩 초기화 에러가 발생했습니다."
    }
}
