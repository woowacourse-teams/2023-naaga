package com.now.naaga.data.firebase.analytics

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

interface AnalyticsDelegate {
    val firebaseAnalytics: FirebaseAnalytics

    fun registerAnalytics(lifeCycle: Lifecycle)
    fun logClickEvent(id: String, name: String)
    fun logServerError(apiName: String, httpCode: Int, errorMessage: String)
}

class DefaultAnalyticsDelegate() : AnalyticsDelegate, DefaultLifecycleObserver {
    override lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun registerAnalytics(lifeCycle: Lifecycle) {
        lifeCycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        firebaseAnalytics = Firebase.analytics
    }

    override fun logClickEvent(id: String, name: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
            param(FirebaseAnalytics.Param.ITEM_ID, id)
            param(FirebaseAnalytics.Param.ITEM_NAME, name)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, BUTTON)
        }
    }

    override fun logServerError(apiName: String, httpCode: Int, errorMessage: String) {
        firebaseAnalytics.logEvent(SERVER_ERROR) {
            param(API_NAME, apiName)
            param(HTTP_STATUS_CODE, "$httpCode")
            param(ERROR_MESSAGE, errorMessage)
        }
    }

    companion object {
        const val BUTTON = "BUTTON"
        const val SERVER_ERROR = "SERVER_ERROR"

        const val API_NAME = "API_NAME"
        const val HTTP_STATUS_CODE = "HTTP_STATUS_CODE"
        const val ERROR_MESSAGE = "ERROR_MESSAGE"
    }
}
