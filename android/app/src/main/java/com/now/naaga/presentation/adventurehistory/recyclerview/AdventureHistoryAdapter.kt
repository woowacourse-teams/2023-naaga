package com.now.naaga.presentation.adventurehistory.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.now.domain.model.AdventureResult
import com.now.naaga.databinding.ItemHistoryBinding

class AdventureHistoryAdapter : ListAdapter<AdventureResult, AdventureHistoryViewHolder>(historyDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdventureHistoryViewHolder {
        val binding =
            ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdventureHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdventureHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val historyDiff = object : DiffUtil.ItemCallback<AdventureResult>() {
            override fun areItemsTheSame(oldItem: AdventureResult, newItem: AdventureResult): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: AdventureResult, newItem: AdventureResult): Boolean {
                return oldItem == newItem
            }
        }
    }
}
