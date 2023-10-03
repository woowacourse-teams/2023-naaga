package com.now.naaga.presentation.mypage.place

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.now.naaga.databinding.CustomMypageGridBinding
import com.now.naaga.presentation.uimodel.model.MyPagePlaceUiModel

class MyPageCustomGrid(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var binding: CustomMypageGridBinding

    private fun initNotEmptyView() {
        layoutInflater = LayoutInflater.from(context)
        binding = CustomMypageGridBinding.inflate(layoutInflater, this, true)
    }

    fun initContent(data: List<MyPagePlaceUiModel>) {
        if (data.isNotEmpty()) {
            initNotEmptyView()
            val adapter = MyPagePlaceAdapter(data)
            binding.rvMypageItemContent.adapter = adapter
        }
    }
}
