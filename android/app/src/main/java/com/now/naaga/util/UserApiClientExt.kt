package com.now.naaga.util

import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

private const val KAKAO_LOGIN_LOG_TAG = "kakao login"
private const val KAKAO_LOGIN_FAIL_MESSAGE = "카카오계정으로 로그인 실패"
private const val KAKAO_LOGIN_SUCCESS_MESSAGE = "카카오계정으로 로그인 성공"

private fun getLoginCallback(inquiryNextAction: () -> Unit): (OAuthToken?, Throwable?) -> Unit {
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.d(KAKAO_LOGIN_LOG_TAG, KAKAO_LOGIN_FAIL_MESSAGE + error)
        } else if (token != null) {
            Log.d(KAKAO_LOGIN_LOG_TAG, KAKAO_LOGIN_SUCCESS_MESSAGE + token.accessToken)
            inquiryNextAction()
        }
    }
    return callback
}

fun loginWithKakao(context: Context, inquiryNextAction: () -> Unit) {
    val callback = getLoginCallback(inquiryNextAction)
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.d(KAKAO_LOGIN_LOG_TAG, KAKAO_LOGIN_FAIL_MESSAGE + error)
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    return@loginWithKakaoTalk
                }
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            } else if (token != null) {
                Log.d(KAKAO_LOGIN_LOG_TAG, KAKAO_LOGIN_SUCCESS_MESSAGE + token.accessToken)
                inquiryNextAction()
            }
        }
    } else {
        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
    }
}
