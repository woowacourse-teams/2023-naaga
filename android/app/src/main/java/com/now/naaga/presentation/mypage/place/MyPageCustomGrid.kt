package com.now.naaga.presentation.mypage.place

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.now.naaga.databinding.CustomMypageGridBinding
import com.now.naaga.databinding.CustomMypageGridEmptyBinding
import com.now.naaga.presentation.uimodel.model.MyPagePlaceUiModel

class MyPageCustomGrid(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var placeBinding: CustomMypageGridBinding
    private lateinit var emptyBinding: CustomMypageGridEmptyBinding

    private fun initPlaceBinding() {
        layoutInflater = LayoutInflater.from(context)
        placeBinding = CustomMypageGridBinding.inflate(layoutInflater, this, true)
    }

    private fun initEmptyBinding() {
        layoutInflater = LayoutInflater.from(context)
        emptyBinding = CustomMypageGridEmptyBinding.inflate(layoutInflater, this, true)
    }

    fun initContent(data: List<MyPagePlaceUiModel>) {
        if (data.isNotEmpty()) {
            initPlaceBinding()
            placeBinding.rvMypageItemContent.adapter = MyPagePlaceAdapter(data)
        } else {
            initEmptyBinding()
        }
    }
}
