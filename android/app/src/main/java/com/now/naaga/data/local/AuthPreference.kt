package com.now.naaga.data.local

interface AuthPreference {
    fun getAccessToken(): String?
    fun setAccessToken(newToken: String)
}
