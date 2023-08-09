package com.now.naaga.data.local

interface AuthDataSource {
    fun getAccessToken(): String?
    fun setAccessToken(newToken: String)
}
