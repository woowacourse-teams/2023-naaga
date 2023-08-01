package com.now.naaga.presentation.rank.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.now.domain.model.Rank
import com.now.naaga.databinding.ItemRankBinding

class RankViewHolder(
    private val binding: ItemRankBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(rank: Rank) {
        binding.rank = rank
    }
}
