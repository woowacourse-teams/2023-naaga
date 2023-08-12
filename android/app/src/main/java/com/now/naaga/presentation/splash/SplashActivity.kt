package com.now.naaga.presentation.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.now.domain.model.AdventureStatus
import com.now.naaga.R
import com.now.naaga.presentation.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var viewModel: SplashViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        viewModel.fetchInProgressAdventure()
        subscribe()
        installSplashScreen()
        setContentView(R.layout.activity_splash)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, SplashViewModel.Factory)[SplashViewModel::class.java]
    }

    private fun subscribe() {
        viewModel.adventureStatus.observe(this) {
            startNextActivity(it)
        }
    }

    private fun startNextActivity(adventureStatus: AdventureStatus) {
        val adventure = viewModel.adventure.value

        val intent = if (adventure == null) {
            LoginActivity.getIntent(this)
        } else {
            LoginActivity.getIntentWithAdventure(this, adventure)
        }
        startActivity(intent)
        finish()
    }
}
