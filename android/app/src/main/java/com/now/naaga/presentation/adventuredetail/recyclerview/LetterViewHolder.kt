package com.now.naaga.presentation.adventuredetail.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.now.naaga.R
import com.now.naaga.databinding.ItemLetterBinding
import com.now.naaga.presentation.uimodel.model.LetterUiModel

class LetterViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_letter, parent, false),
) {
    private val binding: ItemLetterBinding = ItemLetterBinding.bind(itemView)

    fun bind(letter: LetterUiModel) {
        binding.tvItemLetterNickname.text = letter.nickname
        binding.tvItemLetterRegisterDate.text = letter.registerDate
        binding.tvItemLetter.text = letter.message
    }
}
