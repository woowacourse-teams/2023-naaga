package com.now.naaga.presentation.onadventure

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.now.naaga.R
import com.now.naaga.databinding.DialogNaagaAlertBinding
import com.now.naaga.util.getWidthProportionalToDevice

class NaagaAlertDialog private constructor() : DialogFragment() {
    private lateinit var binding: DialogNaagaAlertBinding
    private var title: String? = null
    private var description: String? = null
    private var positiveText: String? = null
    private var negativeText: String? = null
    private lateinit var positiveAction: () -> Unit
    private lateinit var negativeAction: () -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogNaagaAlertBinding.inflate(layoutInflater)
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
        binding.tvAlertDialogPositive.setOnClickListener { positiveAction() }
        binding.tvAlertDialogNegative.setOnClickListener { negativeAction() }
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

    class Builder(private val context: Context) {
        fun buildGiveUpDialog(): NaagaAlertDialog {
            return build(
                title = context.getString(R.string.give_up_dialog_title),
                description = context.getString(R.string.give_up_dialog_description),
                positiveText = context.getString(R.string.give_up_dialog_continue),
                negativeText = context.getString(R.string.give_up_dialog_give_up),
            )
        }

        fun buildHintUseDialog(remainingHintCount: Int): NaagaAlertDialog {
            return build(
                title = context.getString(R.string.hint_using_dialog_title),
                description = context.getString(R.string.hint_using_dialog_description, remainingHintCount),
                positiveText = context.getString(R.string.hint_using_dialog_continue),
                negativeText = context.getString(R.string.hint_using_dialog_give_up),
            )
        }
    }

    companion object {
        private const val WIDTH_RATE = 0.9f

        fun build(
            title: String,
            description: String,
            positiveText: String,
            negativeText: String,
        ): NaagaAlertDialog {
            return NaagaAlertDialog().apply {
                this.title = title
                this.description = description
                this.positiveText = positiveText
                this.negativeText = negativeText
            }
        }
    }
}
