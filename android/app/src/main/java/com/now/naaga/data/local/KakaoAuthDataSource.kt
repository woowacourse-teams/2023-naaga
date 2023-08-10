package com.now.naaga.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class KakaoAuthDataSource(context: Context) : AuthDataSource {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val pref =
        EncryptedSharedPreferences.create(
            context,
            SECRET_SHARED_PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

    override fun getAccessToken(): String {
        return pref.getString(ACCESS_TOKEN_KEY, "") ?: ""
    }

    override fun setAccessToken(newToken: String) {
        pref.edit().putString(ACCESS_TOKEN_KEY, newToken).apply()
    }

    companion object {
        private const val SECRET_SHARED_PREFS_FILE_NAME = "secret_shared_prefs"
        private const val ACCESS_TOKEN_KEY = "access_token_key"
    }
}
