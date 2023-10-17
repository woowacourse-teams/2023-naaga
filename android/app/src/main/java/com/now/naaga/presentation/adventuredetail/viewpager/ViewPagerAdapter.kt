package com.now.naaga.presentation.adventuredetail.viewpager

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.now.naaga.presentation.uimodel.model.OpenLetterUiModel

class ViewPagerAdapter(private val data: List<List<OpenLetterUiModel>>) : RecyclerView.Adapter<ViewPagerHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        return ViewPagerHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
