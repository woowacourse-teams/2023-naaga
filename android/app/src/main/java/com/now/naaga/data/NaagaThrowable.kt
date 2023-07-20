package com.now.naaga.data

sealed class NaagaThrowable(override val message: String?, val userMessage: String) : Throwable() {
    class ServerConnectFailure() :
        NaagaThrowable("서버통신에 실패했습니다. onFailure called", "네트워크에 문제가 있어요.")

    class AuthenticationError(errorMessage: String) :
        NaagaThrowable(errorMessage, "인증 정보에 오류가 발생했습니다. 앱을 다시 실행하세요")

    class UserError(errorMessage: String) :
        NaagaThrowable(errorMessage, "회원 정보에 오류가 발생했습니다. 앱을 다시 실행하세요")

    class PlaceError(errorMessage: String) :
        NaagaThrowable(errorMessage, "목적지에 오류가 발생했습니다. 게임을 다시 시작합니다")

    class GameError(errorMessage: String) :
        NaagaThrowable(errorMessage, "게임에 오류가 발생했습니다. 게임을 다시 시작합니다")

    class NaagaUnknownError(errorMessage: String) : NaagaThrowable(errorMessage, "예상치 못한 오류가 발생했습니다")
}
