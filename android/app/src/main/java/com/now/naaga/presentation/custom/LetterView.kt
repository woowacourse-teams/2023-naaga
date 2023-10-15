package com.now.naaga.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.now.naaga.databinding.CustomLetterViewBinding

class LetterView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    private val binding: CustomLetterViewBinding

    init {
        val layoutInflater = LayoutInflater.from(context)
        binding = CustomLetterViewBinding.inflate(layoutInflater, this, true)
        binding.ivLetterIcon.setOnClickListener { }
        binding.vLetterBackground.setOnClickListener { }
    }

    fun setClickListener(listener: OnClickListener) {
        binding.ivLetterIcon.setOnClickListener(listener)
        binding.vLetterBackground.setOnClickListener(listener)
    }
}
