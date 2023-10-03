package com.now.naaga.presentation.mypage.place

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.now.naaga.R
import com.now.naaga.databinding.RvMypageItemPlaceBinding
import com.now.naaga.presentation.uimodel.model.MyPagePlaceUiModel

class MyPagePlaceViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.rv_mypage_item_place,
        parent,
        false,
    ),
) {
    private val binding = RvMypageItemPlaceBinding.bind(itemView)

    fun bind(data: MyPagePlaceUiModel) {
        binding.model = data
        Glide.with(binding.ivMypageItem).load(data.image).into(binding.ivMypageItem)
        binding.ivMypageItem.clipToOutline = true
    }
}
