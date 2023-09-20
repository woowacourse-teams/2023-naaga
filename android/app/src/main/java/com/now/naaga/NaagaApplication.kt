package com.now.naaga

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.kakao.sdk.common.KakaoSdk
import com.now.naaga.data.local.AuthDataSource
import com.now.naaga.data.local.DefaultAuthDataSource
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NaagaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKakaoSdk()
        initContext()
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

    private fun initContext() {
        context = applicationContext
    }

    companion object DependencyContainer {
        lateinit var context: Context
        lateinit var authDataSource: AuthDataSource
    }
}
