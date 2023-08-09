package com.now.naaga

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.now.naaga.data.local.AuthDataSource
import com.now.naaga.data.local.DefaultAuthDataSource

object NaagaApplication : Application() {
    lateinit var authDataSource: AuthDataSource

    override fun onCreate() {
        super.onCreate()
        initKakaoSdk()
        initDataSources()
    }

    private fun initKakaoSdk() {
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }

    private fun initDataSources() {
        authDataSource = DefaultAuthDataSource(applicationContext)
    }
}
