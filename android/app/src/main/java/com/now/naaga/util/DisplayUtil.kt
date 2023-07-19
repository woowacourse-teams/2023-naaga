package com.now.naaga.util

import android.content.Context

fun getWidthProportionalToDevice(context: Context, rate: Float): Int {
    val display = context.applicationContext.resources.displayMetrics
    val deviceWidth = display.widthPixels
    return (deviceWidth * rate).toInt()
}
