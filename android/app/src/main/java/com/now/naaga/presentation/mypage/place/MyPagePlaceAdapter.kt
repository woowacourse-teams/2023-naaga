package com.now.naaga.presentation.mypage.place

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MyPagePlaceAdapter(
    private val contents: List<MyPageItemUiModel>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return contents.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (contents[position].viewType) {
            MyPageViewType.PLACES -> MyPageViewType.PLACES.ordinal
            MyPageViewType.STATISTICS -> MyPageViewType.STATISTICS.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MyPageViewType.PLACES.ordinal -> MyPagePlaceViewHolder(MyPagePlaceViewHolder.getView(parent))
            MyPageViewType.STATISTICS.ordinal -> MyPageStatisticsViewHolder(MyPageStatisticsViewHolder.getView(parent))
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyPagePlaceViewHolder -> holder.bind(contents[position] as MyPagePlaceUiModel)
            is MyPageStatisticsViewHolder -> holder.bind(contents[position] as MyPageStatisticsUiModel)
        }
    }
}
