package com.now.naaga

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.kakao.sdk.common.KakaoSdk
import com.now.naaga.data.local.AuthDataSource
import com.now.naaga.data.local.DefaultAuthDataSource

class NaagaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKakaoSdk()
        initDataSources()
        disableDarkMode()
    }

    private fun disableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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
