package com.now.naaga.presentation.login

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.now.naaga.BuildConfig

class NaagaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}
