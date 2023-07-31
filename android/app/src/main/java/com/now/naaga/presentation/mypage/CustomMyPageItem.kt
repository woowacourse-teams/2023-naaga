package com.now.naaga.presentation.mypage

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.now.naaga.databinding.CustomMypageItemBinding

class CustomMyPageItem(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var binding: CustomMypageItemBinding
    private lateinit var adapter: MyPageAdapter

    init {
        initView()
    }

    private fun initView() {
        layoutInflater = LayoutInflater.from(context)
        binding = CustomMypageItemBinding.inflate(layoutInflater, this, true)
    }

    fun initContent(data: List<MyPageItemUiModel>) {
        adapter = MyPageAdapter(data)
        binding.rvMypageItemContent.adapter = adapter
        if (data.isNotEmpty()) {
            binding.tvMypageItemTitle.text = data.first().viewType.text
        }
    }
}
