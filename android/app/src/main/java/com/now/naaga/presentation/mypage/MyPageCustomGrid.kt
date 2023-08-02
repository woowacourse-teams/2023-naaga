package com.now.naaga.presentation.mypage

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.now.naaga.databinding.CustomMypageGridBinding

class MyPageCustomGrid(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var binding: CustomMypageGridBinding
    private lateinit var adapter: MyPageAdapter

    private fun initView() {
        layoutInflater = LayoutInflater.from(context)
        binding = CustomMypageGridBinding.inflate(layoutInflater, this, true)
    }

    fun initContent(data: List<MyPageItemUiModel>) {
        if (data.isNotEmpty()) {
            initView()
            makeAdapter(data)
            binding.tvMypageItemTitle.text = data.first().viewType.text
            binding.rvMypageItemContent.adapter = adapter
        }
    }

    private fun makeAdapter(data: List<MyPageItemUiModel>) {
        adapter = if (data.first().viewType == MyPageViewType.PLACES) {
            val lastIndex = if (data.size > END_PLACE_INDEX) END_PLACE_INDEX else data.size
            MyPageAdapter(data.subList(START_PLACE_INDEX, lastIndex))
        } else {
            MyPageAdapter(data)
        }
    }

    companion object {
        const val START_PLACE_INDEX = 0
        const val END_PLACE_INDEX = 3
    }
}
