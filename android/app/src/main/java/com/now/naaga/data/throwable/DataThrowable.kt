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
}
