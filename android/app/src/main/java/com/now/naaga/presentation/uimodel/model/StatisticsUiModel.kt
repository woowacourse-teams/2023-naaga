package com.now.naaga.presentation.uimodel.model

import androidx.annotation.DrawableRes

data class StatisticsUiModel(
    @DrawableRes
    val icon: Int,
    val detail: String,
    val count: Int,
)
