package com.now.naaga.util

import android.content.Context
import kotlin.math.roundToInt

fun getWidthProportionalToDevice(context: Context, rate: Float): Int {
    val display = context.applicationContext.resources.displayMetrics
    val deviceWidth = display.widthPixels
    return (deviceWidth * rate).toInt()
}

fun dpToPx(context: Context, dp: Int): Int {
    val density = context.resources.displayMetrics.density
    return (dp.toFloat() * density).roundToInt()
}
