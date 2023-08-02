package com.now.naaga.presentation.mypage

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.now.naaga.R
import com.now.naaga.databinding.CustomMypageGridBinding
import com.now.naaga.databinding.CustomMypageGridEmptyBinding

class MyPageCustomGrid(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var notEmptybinding: CustomMypageGridBinding
    private lateinit var emptyBinding: CustomMypageGridEmptyBinding
    private lateinit var adapter: MyPageAdapter

    private fun initNotEmptyView() {
        layoutInflater = LayoutInflater.from(context)
        notEmptybinding = CustomMypageGridBinding.inflate(layoutInflater, this, true)
    }

    private fun initEmptyView() {
        layoutInflater = LayoutInflater.from(context)
        emptyBinding = CustomMypageGridEmptyBinding.inflate(layoutInflater, this, true)
    }

    fun initContent(data: List<MyPageItemUiModel>) {
        if (data.isNotEmpty()) {
            initNotEmptyView()
            makeAdapter(data)
            notEmptybinding.tvMypageItemTitle.text = data.first().viewType.text
            notEmptybinding.rvMypageItemContent.adapter = adapter
        } else {
            initEmptyView()
            emptyBinding.tvMypageEmptyItemTitle.text = context.getString(R.string.mypage_empty_item_title)
            emptyBinding.tvMypageEmptyDescription.text = context.getString(R.string.mypage_empty_description)
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
