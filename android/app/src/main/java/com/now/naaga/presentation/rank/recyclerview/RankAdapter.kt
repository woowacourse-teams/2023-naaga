package com.now.naaga.presentation.rank.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.now.domain.model.Rank
import com.now.naaga.databinding.ItemRankBinding

class RankAdapter : ListAdapter<Rank, RankViewHolder>(rankDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankViewHolder {
        val binding =
            ItemRankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RankViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RankViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val rankDiff = object : DiffUtil.ItemCallback<Rank>() {
            override fun areItemsTheSame(oldItem: Rank, newItem: Rank): Boolean {
                return oldItem.player.id == newItem.player.id
            }

            override fun areContentsTheSame(oldItem: Rank, newItem: Rank): Boolean {
                return oldItem == newItem
            }
        }
    }
}
