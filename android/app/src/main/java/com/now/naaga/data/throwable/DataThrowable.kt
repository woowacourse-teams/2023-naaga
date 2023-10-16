package com.now.naaga.data.throwable

sealed class DataThrowable(val code: Int, message: String) : Throwable(message) {
    // 100번대 인증 관련 에러
    class AuthorizationThrowable(code: Int, message: String) : DataThrowable(code, message)

    // 200번대 보편적인 에러
    class UniversalThrowable(code: Int, message: String) : DataThrowable(code, message)

    // 300번대 플레이어 관련 에러
    class PlayerThrowable(code: Int, message: String) : DataThrowable(code, message)

    // 400번대 게임 관련 에러
    class GameThrowable(code: Int, message: String) : DataThrowable(code, message)

    // 500번대 장소 관련 에러
    class PlaceThrowable(code: Int, message: String) : DataThrowable(code, message)

    // http 응답코드 500번대, body가 null일 때의 에러
    class IllegalStateThrowable : DataThrowable(ILLEGAL_STATE_THROWABLE_CODE, ILLEGAL_STATE_THROWABLE_MESSAGE)

    // IO Exception 일 경우의 예외
    class NetworkThrowable : DataThrowable(NETWORK_THROWABLE_CODE, NETWORK_THROWABLE_MESSAGE)

    companion object {
        const val ILLEGAL_STATE_THROWABLE_CODE = 900
        const val ILLEGAL_STATE_THROWABLE_MESSAGE = "잘못된 값입니다."

        const val NETWORK_THROWABLE_CODE = 1000
        const val NETWORK_THROWABLE_MESSAGE = "문제가 발생했어요. 다시 요청해주세요!"

        val hintThrowable = GameThrowable(455, "사용할 수 있는 힌트를 모두 소진했습니다.")
    }
}
