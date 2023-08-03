package com.now.naaga.presentation.uimodel.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerUiModel(
    val id: Long,
    val nickname: String,
    val score: Int,
) : Parcelable
