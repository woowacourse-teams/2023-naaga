package com.now.naaga.data

sealed class NaagaThrowable(override val message: String?) : Throwable() {
    class ClientError(val code: Int, message: String) : NaagaThrowable(message)
    class BackEndError() : NaagaThrowable("500에러")
    class ServerConnectFailure() :
        NaagaThrowable("서버통신에 실패했습니다. onFailure called")

    class NaagaUnknownError(errorMessage: String) : NaagaThrowable(errorMessage)
}
