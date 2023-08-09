package com.now.domain.model

enum class AuthPlatformType {
    KAKAO,
    NONE, ;

    companion object {
        fun findByName(text: String): AuthPlatformType {
            return AuthPlatformType.values().find { it.name == text } ?: NONE
        }
    }
}
