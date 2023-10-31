package com.now.naaga.presentation.adventurehistory.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.now.domain.model.AdventureResult
import com.now.domain.model.type.AdventureResultType
import com.now.naaga.R
import com.now.naaga.databinding.ItemHistoryBinding

class AdventureHistoryViewHolder(
    parent: ViewGroup,
    onClick: (Int) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false),
) {

    private val binding: ItemHistoryBinding = ItemHistoryBinding.bind(itemView)

    init {
        binding.root.setOnClickListener { onClick(adapterPosition) }
    }

    fun bind(adventureResult: AdventureResult) {
        binding.adventureResult = adventureResult
        Glide.with(binding.ivAdventureHistoryPhoto)
            .load(adventureResult.destination.image)
            .into(binding.ivAdventureHistoryPhoto)
        binding.ivAdventureHistoryPhoto.clipToOutline = true
        setSuccessOrFailure(adventureResult.resultType)
        setName(adventureResult)
    }

    private fun setSuccessOrFailure(resultType: AdventureResultType) {
        when (resultType) {
            AdventureResultType.SUCCESS -> binding.ivAdventureHistoryStamp.setImageResource(R.drawable.ic_success_label)
            AdventureResultType.FAIL -> binding.ivAdventureHistoryStamp.setImageResource(R.drawable.ic_fail_label)
            AdventureResultType.NONE -> {}
        }
    }

    private fun setName(adventureResult: AdventureResult) {
        when (adventureResult.resultType) {
            AdventureResultType.SUCCESS -> binding.tvAdventureHistoryName.text = adventureResult.destination.name
            AdventureResultType.FAIL -> binding.tvAdventureHistoryName.text = DESTINATION_NAME_IN_FAILURE_CASE
            AdventureResultType.NONE -> {}
        }
    }

    companion object {
        private const val DESTINATION_NAME_IN_FAILURE_CASE = "????"
    }
}
