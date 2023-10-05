package com.now.naaga.presentation.uimodel.model

import androidx.annotation.DrawableRes
import com.now.naaga.R

data class StatisticsUiModel(
    @DrawableRes val icon: Int,
    @DrawableRes val background: Int,
    val detail: String,
    val count: Int,
) {
    companion object {
        fun getSuccessAdventureStatisticsModel(count: Int): StatisticsUiModel {
            return StatisticsUiModel(
                R.drawable.ic_success_adventure,
                R.drawable.oval_orange_gradient,
                SUCCESS_DETAIL,
                count,
            )
        }

        fun getFailAdventureStatisticsModel(count: Int): StatisticsUiModel {
            return StatisticsUiModel(
                R.drawable.ic_fail_adventure,
                R.drawable.oval_yellow_gradient,
                FAIL_DETAIL,
                count,
            )
        }
        fun getAllAdventureStatisticsModel(count: Int): StatisticsUiModel {
            return StatisticsUiModel(
                R.drawable.ic_all_adventure,
                R.drawable.oval_blue_gradient,
                ALL_DETAIL,
                count,
            )
        }

        private const val SUCCESS_DETAIL = "성공 모험"
        private const val FAIL_DETAIL = "실패 모험"
        private const val ALL_DETAIL = "전체 모험"
    }
}
