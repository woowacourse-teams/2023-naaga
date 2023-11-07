package com.now.domain.model

@JvmInline
value class PreferenceCount(val value: Int) {
    init {
        require(value >= 0) { "선호도 개수가 0보다 작을 수 없습니다." }
    }

    fun plus(): PreferenceCount {
        return PreferenceCount(value + 1)
    }

    fun minus(): PreferenceCount {
        val newValue = if (value == 0) 0 else (value - 1)
        return PreferenceCount(newValue)
    }
}
