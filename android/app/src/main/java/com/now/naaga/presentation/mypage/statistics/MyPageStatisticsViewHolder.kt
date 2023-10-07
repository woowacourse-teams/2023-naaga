package com.now.naaga.presentation.mypage.statistics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.now.naaga.R
import com.now.naaga.databinding.RvMypageItemAdventureBinding
import com.now.naaga.presentation.uimodel.model.StatisticsUiModel

class MyPageStatisticsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.rv_mypage_item_adventure, parent, false),
) {
    private val binding = RvMypageItemAdventureBinding.bind(itemView)

    fun bind(statisticsUiModels: StatisticsUiModel) {
        binding.apply {
            ivMypageItemIcon.setImageDrawable(
                AppCompatResources.getDrawable(itemView.context, statisticsUiModels.icon),
            )
            tvMypageItemAdventureCount.text = statisticsUiModels.count.toString()
            tvMypageItemAdventureTitle.text = statisticsUiModels.detail
            vMypageItemIconBackground.background =
                AppCompatResources.getDrawable(itemView.context, statisticsUiModels.background)
        }
    }
}
