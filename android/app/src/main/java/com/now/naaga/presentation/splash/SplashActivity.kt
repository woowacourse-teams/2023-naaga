package com.now.naaga.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.now.domain.model.AdventureStatus.DONE
import com.now.domain.model.AdventureStatus.IN_PROGRESS
import com.now.domain.model.AdventureStatus.NONE
import com.now.domain.model.Game
import com.now.naaga.R
import com.now.naaga.presentation.beginadventure.BeginAdventureActivity
import com.now.naaga.presentation.onadventure.OnAdventureActivity

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
        viewModel.adventure.observe(this) {
            startNextActivity(it)
        }
    }

    private fun startNextActivity(adventure: Game) {
        val intent: Intent = when (adventure.adventureStatus) {
            IN_PROGRESS -> OnAdventureActivity.getIntentWithAdventure(this, adventure)
            DONE -> Intent(this, BeginAdventureActivity::class.java)
            NONE -> Intent(this, BeginAdventureActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
