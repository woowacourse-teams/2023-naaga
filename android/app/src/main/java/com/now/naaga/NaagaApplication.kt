package com.now.naaga

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.now.naaga.data.local.AuthDataSource
import com.now.naaga.data.local.DefaultAuthDataSource
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NaagaApplication : Application() {

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

    companion object DependencyContainer {
        lateinit var authDataSource: AuthDataSource
    }
}
