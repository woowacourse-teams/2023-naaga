package com.now.domain.model

enum class Direction {
    NORTH,
    EAST,
    WEST,
    SOUTH,
    NONE, ;

    companion object {
        fun findByName(text: String): Direction {
            return values().find { it.name == text } ?: NONE
        }
    }
}
