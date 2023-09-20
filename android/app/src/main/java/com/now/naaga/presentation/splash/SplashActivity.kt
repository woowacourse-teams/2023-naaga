package com.now.naaga.presentation.splash

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.now.naaga.R
import com.now.naaga.presentation.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchInProgressAdventure()
        subscribe()
        installSplashScreen()
        setContentView(R.layout.activity_splash)
    }

    private fun subscribe() {
        viewModel.adventureStatus.observe(this) {
            startNextActivity()
        }
    }

    private fun startNextActivity() {
        val intent = LoginActivity.getIntent(this)
        startActivity(intent)
        finish()
    }
}
