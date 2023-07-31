package com.now.domain.model

@JvmInline
value class RemainingTryCount(private val value: Int) {
    init {
        require(value >= 0) { "잔여 시도 횟수는 0보다 작을 수 없습니다." }
    }

    fun toInt(): Int = value

    operator fun minus(value: Int): RemainingTryCount {
        var result = this.value - value
        if (result < 0) result = 0
        return RemainingTryCount(result)
    }
}
