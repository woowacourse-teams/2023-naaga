package com.now.naaga.presentation.mypage.statistics

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.now.naaga.presentation.uimodel.model.StatisticsUiModel

class MyPageStatisticsAdapter(private val statisticsUiModels: List<StatisticsUiModel>) :
    RecyclerView.Adapter<MyPageStatisticsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPageStatisticsViewHolder {
        return MyPageStatisticsViewHolder(parent)
    }

    override fun onBindViewHolder(holder: MyPageStatisticsViewHolder, position: Int) {
        holder.bind(statisticsUiModels[position])
    }

    override fun getItemCount(): Int {
        return statisticsUiModels.size
    }
}
