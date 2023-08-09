package com.now.naaga.data.remote.retrofit

import com.now.naaga.data.throwable.DataThrowable.AuthorizationThrowable
import com.now.naaga.data.throwable.DataThrowable.GameThrowable
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

fun <T> Call<T>.fetchResponse(
    onSuccess: (T) -> Unit,
    onFailure: (Throwable) -> Unit,
) {
    enqueue(
        object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    // TODO : body가 없는 경우 뱉어낼 에러 정하기 & 500번 에러도 고민
                    // TODO : 실패로 던져주고 처리는 뷰에서 진행한다.
                    val body = response.body() ?: return
                    onSuccess(body)
                }

                if (response.codeIn400s()) {
                    val errorResponse = response.errorBody()?.string()
                    val jsonObject = errorResponse?.let { JSONObject(it) }
                    val code = jsonObject?.getInt("code") ?: 0
                    val message = jsonObject?.getString("message") ?: ""

                    when (code) {
                        in 100 until 200 -> {
                            onFailure(AuthorizationThrowable(code, message))
                        }
                        in 200 until 300 -> {
                            onFailure(UniversalThrowable(code, message))
                        }
                        in 300 until 400 -> {
                            onFailure(PlayerThrowable(code, message))
                        }
                        in 400 until 500 -> {
                            onFailure(GameThrowable(code, message))
                        }
                        in 500 until 600 -> {
                            onFailure(PlaceThrowable(code, message))
                        }
                        in 900 until 1000 -> {
                            onFailure(UniversalThrowable(code, message))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                onFailure(t)
            }
        },
    )
}
