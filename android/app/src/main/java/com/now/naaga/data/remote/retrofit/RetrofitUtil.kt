package com.now.naaga.data.remote.retrofit

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.now.naaga.data.NaagaThrowable
import com.now.naaga.data.NaagaThrowable.NaagaUnknownError
import com.now.naaga.data.NaagaThrowable.ServerConnectFailure
import com.now.naaga.data.remote.dto.FailureDto
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
