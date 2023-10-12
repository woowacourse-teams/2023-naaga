package com.now.naaga.util

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbarWithEvent(message: String, eventTitle: String, event: () -> Unit) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        .setAction(eventTitle) {
            event()
        }.setAnimationMode(ANIMATION_MODE_SLIDE).show()
}
