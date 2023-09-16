package com.now.naaga.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class DefaultAuthDataSource(context: Context) : AuthDataSource {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val authPreference =
        EncryptedSharedPreferences.create(
            context,
            AUTH_ENCRYPTED_PREFERENCE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

    override fun getAccessToken(): String? {
        return authPreference.getString(ACCESS_TOKEN, null)
    }

    override fun setAccessToken(newToken: String) {
        authPreference.edit().putString(ACCESS_TOKEN, newToken).apply()
    }

    override fun getRefreshToken(): String? {
        return authPreference.getString(REFRESH_TOKEN, null)
    }

    override fun setRefreshToken(newToken: String) {
        authPreference.edit().putString(REFRESH_TOKEN, newToken).apply()
    }

    override fun resetToken() {
        authPreference.edit().putString(ACCESS_TOKEN, DEFAULT_TOKEN).apply()
        authPreference.edit().putString(REFRESH_TOKEN, DEFAULT_TOKEN).apply()
    }

    companion object {
        private const val DEFAULT_TOKEN = ""
        private const val AUTH_ENCRYPTED_PREFERENCE = "AUTH_ENCRYPTED_PREFERENCE"
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val REFRESH_TOKEN = "REFRESH_TOKEN"
    }
}
