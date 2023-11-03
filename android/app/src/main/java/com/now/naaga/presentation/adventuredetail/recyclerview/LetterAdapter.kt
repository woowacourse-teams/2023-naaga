package com.now.naaga.presentation.adventuredetail.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.now.naaga.presentation.uimodel.model.LetterUiModel

class LetterAdapter(private val letters: List<LetterUiModel>) : RecyclerView.Adapter<LetterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterViewHolder {
        return LetterViewHolder(parent)
    }

    override fun onBindViewHolder(holder: LetterViewHolder, position: Int) {
        holder.bind(letters[position])
    }

    override fun getItemCount(): Int {
        return letters.size
    }
}
