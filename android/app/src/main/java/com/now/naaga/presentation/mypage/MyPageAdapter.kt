package com.now.naaga.presentation.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.now.naaga.databinding.RvMypageItemPlaceBinding
import com.now.naaga.databinding.RvMypageItemStatisticsBinding

class MyPageAdapter(
    private val contents: List<MyPageItemUiModel>,
) : RecyclerView.Adapter<MyPageAdapter.MyPageStatisticsViewHolder>() {

    class MyPagePlaceViewHolder(private val binding: RvMypageItemPlaceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MyPagePlaceUiModel) {
            binding.model = data
            Glide.with(binding.ivMypageItem).load(data.image).into(binding.ivMypageItem)
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
        }

        companion object {
            fun getView(parent: ViewGroup): RvMypageItemStatisticsBinding {
                val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
                return RvMypageItemStatisticsBinding.inflate(layoutInflater, parent, false)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPageStatisticsViewHolder {
        return MyPageStatisticsViewHolder(MyPageStatisticsViewHolder.getView(parent))
    }

    override fun getItemCount(): Int {
        return contents.size
    }

    override fun onBindViewHolder(holder: MyPageStatisticsViewHolder, position: Int) {
        holder.bind(contents[position] as MyPageStatisticsUiModel)
    }
}
