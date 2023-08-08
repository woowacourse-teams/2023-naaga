package com.now.naaga.data.local

import android.content.Context
import android.content.SharedPreferences

class KakaoAuthPreference(context: Context): AuthPreference {
    private val pref: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    override fun getAccessToken(): String? {
        return pref.getString(ACCESS_TOKEN_KEY, "")
    }

    override fun setAccessToken(newToken: String) {
        pref.edit().putString(ACCESS_TOKEN_KEY, newToken).apply()
    }

    companion object {
        private const val PREFERENCE_NAME = "ACCESS_TOKEN_"
        private const val ACCESS_TOKEN_KEY = "access_token_key"
    }
}
