package com.now.naaga.data.remote.retrofit

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.now.naaga.data.NaagaThrowable
import com.now.naaga.data.NaagaThrowable.NaagaUnknownError
import com.now.naaga.data.NaagaThrowable.ServerConnectFailure
import com.now.naaga.data.remote.dto.FailureDto
import com.now.naaga.data.throwable.DataThrowable.AuthorizationThrowable
import com.now.naaga.data.throwable.DataThrowable.GameThrowable
import com.now.naaga.data.throwable.DataThrowable.PlaceThrowable
import com.now.naaga.data.throwable.DataThrowable.PlayerThrowable
import com.now.naaga.data.throwable.DataThrowable.UniversalThrowable
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val ERROR_500: String = "500에러 발생"
const val ERROR_NOT_400_500: String = "400, 500대가 아닌 오류 발생"

fun <T> Response<T>.isFailure400(): Boolean {
    if (this.code() in 400..499) return true
    return false
}

fun <T> Response<T>.isFailure500(): Boolean {
    if (this.code() >= 500) return true
    return false
}

fun <T> Response<T>.getFailureDto(): FailureDto {
    val errorString = this.errorBody()?.string()
    val jsonObject: JsonObject = JsonParser.parseString(errorString).asJsonObject
    val failureDto: FailureDto = Gson().fromJson(jsonObject, FailureDto::class.java)
    return failureDto
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

                if (response.code() == 400) {
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

fun <T> Call<T>.fetchNaagaNullableResponse(
    onSuccess: (T?) -> Unit,
    onFailure: (NaagaThrowable) -> Unit,
) {
    enqueue(
        object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    onSuccess(response.body())
                } else {
                    if (response.isFailure400()) {
                        val failureDto = response.getFailureDto()
                        onFailure(NaagaThrowable.ClientError(failureDto.code, failureDto.message))
                        return
                    }
                    if (response.isFailure500()) {
                        onFailure(NaagaThrowable.BackEndError())
                        return
                    }
                    onFailure(NaagaUnknownError(ERROR_NOT_400_500))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                onFailure(ServerConnectFailure())
            }
        },
    )
}

fun <T> Call<T>.fetchNaagaResponse(
    onSuccess: (T) -> Unit,
    onFailure: (NaagaThrowable) -> Unit,
) {
    enqueue(
        object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body == null) {
                        onFailure(NaagaUnknownError("response body가 null입니다."))
                        return
                    }
                    onSuccess(body)
                } else {
                    if (response.isFailure400()) {
                        val failureDto = response.getFailureDto()
                        onFailure(NaagaThrowable.ClientError(failureDto.code, failureDto.message))
                        return
                    }
                    if (response.isFailure500()) {
                        onFailure(NaagaThrowable.BackEndError())
                        return
                    }
                    onFailure(NaagaUnknownError(ERROR_NOT_400_500))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                onFailure(ServerConnectFailure())
            }
        },
    )
}
