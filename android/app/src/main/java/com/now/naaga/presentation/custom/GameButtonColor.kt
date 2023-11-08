package com.now.naaga.presentation.custom

import androidx.annotation.ColorRes
import com.now.naaga.R

enum class GameButtonColor(
    @ColorRes val mainColor: Int,
    @ColorRes val firstShadowColor: Int,
    @ColorRes val middleColor: Int,
    @ColorRes val secondShadowColor: Int,
    @ColorRes val bottomColor: Int,
) {
    YELLOW(
        R.color.custom_button_yellow_main,
        R.color.custom_button_yellow_first_shadow,
        R.color.custom_button_yellow_middle,
        R.color.custom_button_yellow_second_shadow,
        R.color.custom_button_yellow_bottom,
    ),
    BLUE(
        R.color.custom_button_blue_main,
        R.color.custom_button_blue_first_shadow,
        R.color.custom_button_blue_middle,
        R.color.custom_button_blue_second_shadow,
        R.color.custom_button_blue_bottom,
    ), ;

    companion object {
        fun getColor(ordinal: Int): GameButtonColor {
            return values().find { it.ordinal == ordinal } ?: YELLOW
        }
    }
}
