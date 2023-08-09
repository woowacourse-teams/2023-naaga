package com.now.naaga.presentation.login

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import com.now.naaga.BuildConfig

class NaagaApplication : Application() {
    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }

    companion object {
        private var instance: NaagaApplication? = null
        fun getContext(): Context {
            return instance?.applicationContext ?: throw IllegalStateException()
        }
    }
}
