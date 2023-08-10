package com.now.naaga.data.remote.retrofit

import com.now.naaga.data.throwable.DataThrowable.AuthorizationThrowable
import com.now.naaga.data.throwable.DataThrowable.GameThrowable
import com.now.naaga.data.throwable.DataThrowable.IllegalStateThrowable
import com.now.naaga.data.throwable.DataThrowable.PlaceThrowable
import com.now.naaga.data.throwable.DataThrowable.PlayerThrowable
import com.now.naaga.data.throwable.DataThrowable.UniversalThrowable
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> Response<T>.codeIn400s(): Boolean {
    return this.code() in 400..499
}

fun <T> Response<T>.codeIn500s(): Boolean {
    return this.code() in 500..599
}

fun <T> Call<T>.fetchResponse(
    onSuccess: (T) -> Unit,
    onFailure: (Throwable) -> Unit,
) {
    enqueue(
        object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val body = response.body() ?: return onFailure(IllegalStateThrowable())
                    onSuccess(body)
                }

                if (response.codeIn500s()) {
                    return onFailure(IllegalStateThrowable())
                }

                if (response.codeIn400s()) {
                    val errorResponse = response.errorBody()?.string()
                    val jsonObject = errorResponse?.let { JSONObject(it) }
                    val code = jsonObject?.getInt("code") ?: 0
                    val message = jsonObject?.getString("message") ?: ""

                    when (code) {
                        in 100..199 -> { onFailure(AuthorizationThrowable(code, message)) }
                        in 200..299 -> { onFailure(UniversalThrowable(code, message)) }
                        in 300..399 -> { onFailure(PlayerThrowable(code, message)) }
                        in 400..499 -> { onFailure(GameThrowable(code, message)) }
                        in 500..599 -> { onFailure(PlaceThrowable(code, message)) }
                    }
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                onFailure(IllegalStateThrowable())
            }
        },
    )
}
