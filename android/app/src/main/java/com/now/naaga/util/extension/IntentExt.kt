package com.now.naaga.util.extension

import android.content.Intent
import android.os.Build

fun <T> Intent.getParcelableCompat(key: String, data: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, data)
    } else {
        getParcelableExtra(key) as T?
    }
}
