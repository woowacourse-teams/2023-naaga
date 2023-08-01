package com.now.naaga.presentation.adventurehistory.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.now.domain.model.AdventureResult
import com.now.domain.model.AdventureResultType
import com.now.naaga.R
import com.now.naaga.databinding.ItemHistoryBinding

class AdventureHistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(adventureResult: AdventureResult) {
        binding.adventureResult = adventureResult
        setSuccessOrFailure(adventureResult.resultType)
        setName(adventureResult)
    }

    private fun setSuccessOrFailure(resultType: AdventureResultType) {
        when (resultType) {
            AdventureResultType.SUCCESS -> binding.ivAdventureHistoryStamp.setImageResource(R.drawable.ic_success)
            AdventureResultType.FAIL -> binding.ivAdventureHistoryStamp.setImageResource(R.drawable.ic_fail)
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

        fun getView(parent: ViewGroup): ItemHistoryBinding {
            val layoutInflater = LayoutInflater.from(parent.context)
            return ItemHistoryBinding.inflate(layoutInflater, parent, false)
        }
    }
}
