package com.now.naaga.presentation.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.now.naaga.databinding.RvMypageItemBinding

class MyPageAdapter(
    private val contents: List<MyPageItemUiModel>,
) : RecyclerView.Adapter<MyPageAdapter.MyPageViewHolder>() {

    class MyPageViewHolder(private val binding: RvMypageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MyPageItemUiModel) {
            when (data) {
                is MyPagePlaceUiModel -> bindPlaceModel(data)
                is MyPageStatisticsUiModel -> bindStatisticsModel(data)
            }
        }

        private fun bindPlaceModel(data: MyPagePlaceUiModel) {
            binding.ivMypageItem.clipToOutline = true
            Glide.with(binding.ivMypageItem).load(data.image).into(binding.ivMypageItem)
            binding.tvMypageItemDescription.text = data.description
            binding.clMypageItemValue.visibility = View.INVISIBLE
        }

        private fun bindStatisticsModel(data: MyPageStatisticsUiModel) {
            binding.tvMapygeItemValue.text = data.value.toString()
            binding.tvMypageItemUnit.text = data.unit
            binding.tvMypageItemDescription.text = data.description
            binding.ivMypageItem.visibility = View.INVISIBLE
        }

        companion object {
            fun getView(parent: ViewGroup): RvMypageItemBinding {
                val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
                return RvMypageItemBinding.inflate(layoutInflater, parent, false)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPageViewHolder {
        return MyPageViewHolder(MyPageViewHolder.getView(parent))
    }

    override fun getItemCount(): Int {
        return contents.size
    }

    override fun onBindViewHolder(holder: MyPageViewHolder, position: Int) {
        holder.bind(contents[position])
    }
}
