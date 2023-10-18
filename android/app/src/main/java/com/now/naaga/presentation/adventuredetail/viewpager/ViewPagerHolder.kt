package com.now.naaga.presentation.adventuredetail.viewpager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.now.naaga.R
import com.now.naaga.databinding.ItemViewPagerBinding
import com.now.naaga.presentation.adventuredetail.recyclerview.LetterAdapter
import com.now.naaga.presentation.uimodel.model.LetterUiModel

class ViewPagerHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_view_pager, parent, false),
) {
    private val binding: ItemViewPagerBinding = ItemViewPagerBinding.bind(itemView)

    fun bind(data: List<LetterUiModel>) {
        binding.rvItemViewPager.adapter = LetterAdapter(data)
    }
}
