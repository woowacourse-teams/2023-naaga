package com.now.domain.model.type

enum class AdventureResultType {
    SUCCESS,
    FAIL,
    NONE, ;

    companion object {
        fun findByName(text: String): AdventureResultType {
            return values().find { it.name == text } ?: NONE
        }
    }
}
