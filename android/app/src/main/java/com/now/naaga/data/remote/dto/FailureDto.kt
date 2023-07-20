package com.now.naaga.data.remote.dto

import com.now.naaga.data.NaagaThrowable
import kotlinx.serialization.Serializable

@Serializable
data class FailureDto(
    val code: Int,
    val message: String,
) {
    fun getThrowable(): NaagaThrowable {
        return when (code) {
            in 100..199 -> NaagaThrowable.AuthenticationError(message)
            in 200..299 -> NaagaThrowable.UserError(message)
            in 300..399 -> NaagaThrowable.PlaceError(message)
            in 400..499 -> NaagaThrowable.GameError(message)
            else -> NaagaThrowable.NaagaUnknownError(message)
        }
    }
}
