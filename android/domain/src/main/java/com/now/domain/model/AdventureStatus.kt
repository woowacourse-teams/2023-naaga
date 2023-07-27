package com.now.domain.model

enum class AdventureStatus {
    DONE,
    IN_PROGRESS,
    ERROR
    ;

    companion object {
        fun getStatus(status: String): AdventureStatus {
            return when (status) {
                DONE.name -> DONE
                IN_PROGRESS.name -> IN_PROGRESS
                else -> ERROR
            }
        }
    }
}