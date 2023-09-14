package com.now.naaga.presentation.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.now.naaga.R
import com.now.naaga.presentation.beginadventure.BeginAdventureActivity
import com.now.naaga.presentation.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var viewModel: SplashViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        viewModel.refreshAuth()
        subscribe()
        setContentView(R.layout.activity_splash)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, SplashViewModel.Factory)[SplashViewModel::class.java]
    }

    private fun subscribe() {
        viewModel.event.observe(this) {
            when (it) {
                is SplashViewModel.Event.RefreshSucceed -> startBeginAdventureActivity()
                is SplashViewModel.Event.RefreshFailed -> startLoginActivity()
            }
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
