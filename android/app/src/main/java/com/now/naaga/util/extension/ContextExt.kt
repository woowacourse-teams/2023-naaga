package com.now.naaga.util.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast

fun Context.openSetting() {
    val appDetailsIntent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:$packageName"),
    ).addCategory(Intent.CATEGORY_DEFAULT)
    startActivity(appDetailsIntent)
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
