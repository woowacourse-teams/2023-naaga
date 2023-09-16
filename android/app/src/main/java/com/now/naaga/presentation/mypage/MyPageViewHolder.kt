package com.now.naaga.presentation.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.now.naaga.databinding.RvMypageItemPlaceBinding
import com.now.naaga.databinding.RvMypageItemStatisticsBinding

class MyPagePlaceViewHolder(private val binding: RvMypageItemPlaceBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: MyPagePlaceUiModel) {
        binding.model = data
        Glide.with(binding.ivMypageItem).load(data.image).into(binding.ivMypageItem)
        binding.ivMypageItem.clipToOutline = true
    }

    companion object {
        fun getView(parent: ViewGroup): RvMypageItemPlaceBinding {
            val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
            return RvMypageItemPlaceBinding.inflate(layoutInflater, parent, false)
        }
    }
}

class MyPageStatisticsViewHolder(private val binding: RvMypageItemStatisticsBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: MyPageStatisticsUiModel) {
        binding.model = data

        // 숫자가 1자리일 때 36, 2자리일 때 32 ... 이렇게 4단위로 내려가도록 계산함.
        // 단 10자리 부터는 0혹은 음수가 나올 수 있음
        val fontSize = (10 - data.value.toString().length) * 4
        binding.tvMypageItemValue.textSize = fontSize.toFloat()
    }

    companion object {
        fun getView(parent: ViewGroup): RvMypageItemStatisticsBinding {
            val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
            return RvMypageItemStatisticsBinding.inflate(layoutInflater, parent, false)
        }
    }
}
