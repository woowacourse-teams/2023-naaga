package com.now.naaga.presentation.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.now.naaga.R
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.firebase.analytics.SPLASH_MY_PAGE_STATISTICS
import com.now.naaga.presentation.beginadventure.BeginAdventureActivity
import com.now.naaga.presentation.login.LoginActivity

class SplashActivity : AppCompatActivity(), AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var viewModel: SplashViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerAnalytics(this.lifecycle)
        initViewModel()
        viewModel.testTokenValid()
        subscribe()
        setContentView(R.layout.activity_splash)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, SplashViewModel.Factory)[SplashViewModel::class.java]
    }

    private fun subscribe() {
        viewModel.isTokenValid.observe(this) { isTokenValid: Boolean ->
            if (isTokenValid) {
                startBeginAdventureActivity()
                return@observe
            }
            startLoginActivity()
        }
        viewModel.error.observe(this) {
            logServerError(SPLASH_MY_PAGE_STATISTICS, it.code, it.message.toString())
        }
    }

    private fun startBeginAdventureActivity() {
        startActivity(BeginAdventureActivity.getIntent(this))
        finish()
    }

    private fun startLoginActivity() {
        startActivity(LoginActivity.getIntent(this))
        finish()
    }
}
