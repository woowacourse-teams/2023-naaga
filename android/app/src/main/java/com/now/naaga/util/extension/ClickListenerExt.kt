package com.now.naaga.util.extension

import com.naver.maps.map.overlay.Overlay
import com.now.naaga.util.singleclickevent.OnSingleClickListener

fun Overlay.setOnSingleClickListener(action: (overlay: Overlay) -> Unit) {
    val clickListener = OnSingleClickListener {
        action(it)
    }
    onClickListener = clickListener
}
