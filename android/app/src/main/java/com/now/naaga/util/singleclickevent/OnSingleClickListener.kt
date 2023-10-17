package com.now.naaga.util.singleclickevent

import com.naver.maps.map.overlay.Overlay

class OnSingleClickListener(private val onSingleClick: (overlay: Overlay) -> Unit) : Overlay.OnClickListener {
    private var lastClickTime = DEFAULT_LAST_CLICK_TIME

    override fun onClick(overlay: Overlay): Boolean {
        if (isValidate()) {
            onSingleClick(overlay)
        }
        lastClickTime = System.currentTimeMillis()
        return true
    }

    private fun isValidate(): Boolean {
        return System.currentTimeMillis() - lastClickTime > CLICK_INTERVAL_TIME
    }

    companion object {
        private const val DEFAULT_LAST_CLICK_TIME = 0L
        private const val CLICK_INTERVAL_TIME = 1000L
    }
}
