package com.now.naaga.presentation.mypage.place

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.now.naaga.presentation.uimodel.model.MyPagePlaceUiModel

class MyPagePlaceAdapter(
    private val contents: List<MyPagePlaceUiModel>,
) : RecyclerView.Adapter<MyPagePlaceViewHolder>() {

    override fun getItemCount(): Int {
        return contents.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPagePlaceViewHolder {
        return MyPagePlaceViewHolder(parent)
    }

    override fun onBindViewHolder(holder: MyPagePlaceViewHolder, position: Int) {
        holder.bind(contents[position])
    }
}
