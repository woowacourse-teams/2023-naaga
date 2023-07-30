package com.now.domain.model

@JvmInline
value class RestTryCount(val value: Int) {
    operator fun minus(value: Int): RestTryCount {
        return RestTryCount(this.value - value)
    }
}
