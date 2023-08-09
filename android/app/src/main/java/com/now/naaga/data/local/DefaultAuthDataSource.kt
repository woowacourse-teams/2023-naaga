package com.now.naaga.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class DefaultAuthDataSource(context: Context) : AuthDataSource {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val pref =
        EncryptedSharedPreferences.create(
            context,
            AUTH_ENCRYPTED_PREFERENCE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

    override fun getAccessToken(): String? {
        return pref.getString(ACCESS_TOKEN_KEY, null)
    }

    override fun setAccessToken(newToken: String) {
        pref.edit().putString(ACCESS_TOKEN_KEY, newToken).apply()
    }

    companion object {
        private const val AUTH_ENCRYPTED_PREFERENCE = "AUTH_ENCRYPTED_PREFERENCE"
        private const val ACCESS_TOKEN_KEY = "access_token_key"
    }
}
