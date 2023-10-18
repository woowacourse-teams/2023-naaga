package com.now.naaga.util.extension

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar

fun View.showShortSnackbarWithEvent(message: String, actionTitle: String, action: () -> Unit) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        .setAction(actionTitle) {
            action()
        }.setAnimationMode(ANIMATION_MODE_SLIDE).show()
}

fun View.showSnackbarWithEvent(message: String, actionTitle: String, length: Int, action: () -> Unit) {
    Snackbar.make(this, message, length)
        .setAction(actionTitle) {
            action()
        }.setAnimationMode(ANIMATION_MODE_SLIDE).show()
}

fun View.showSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).setAnimationMode(ANIMATION_MODE_SLIDE).show()
}
