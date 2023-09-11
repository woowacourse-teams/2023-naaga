package com.now.naaga.util

import com.now.naaga.data.throwable.DataThrowable
import org.json.JSONObject
import retrofit2.Response

private fun <T> Response<T>.codeIn400s(): Boolean {
    return this.code() in 400..499
}

private fun <T> Response<T>.codeIn500s(): Boolean {
    return this.code() in 500..599
}

fun <T> Response<T>.getValueOrThrow(): T {
    if (this.isSuccessful) {
        return this.body() ?: throw DataThrowable.IllegalStateThrowable()
    }

    if (codeIn500s()) {
        throw DataThrowable.IllegalStateThrowable()
    }

    if (codeIn400s()) {
        val errorResponse = errorBody()?.string()
        val jsonObject = errorResponse?.let { JSONObject(it) }
        val code = jsonObject?.getInt("code") ?: 0
        val message = jsonObject?.getString("message") ?: ""

        when (code) {
            in 100..199 -> { throw DataThrowable.AuthorizationThrowable(code, message) }
            in 200..299 -> { throw DataThrowable.UniversalThrowable(code, message) }
            in 300..399 -> { throw DataThrowable.PlayerThrowable(code, message) }
            in 400..499 -> { throw DataThrowable.GameThrowable(code, message) }
            in 500..599 -> { throw DataThrowable.PlaceThrowable(code, message) }
        }
    }
    throw DataThrowable.IllegalStateThrowable()
}
