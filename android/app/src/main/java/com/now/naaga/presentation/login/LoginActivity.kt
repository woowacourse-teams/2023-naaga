package com.now.naaga.presentation.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.now.naaga.R
import com.now.naaga.databinding.ActivityLoginBinding
import com.now.naaga.presentation.beginadventure.BeginAdventureActivity
import com.now.naaga.util.loginWithKakao

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setClickListeners()
        setStatusBar()
    }

    private fun setClickListeners() {
        binding.ivLoginKakao.setOnClickListener {
            loginWithKakao(this) { navigateHome() }
        }
    }

    private fun setStatusBar() {
        window.apply {
            statusBarColor = getColor(R.color.white)
            WindowInsetsControllerCompat(this, this.decorView).isAppearanceLightStatusBars = true
        }
    }

    private fun navigateHome() {
        startActivity(BeginAdventureActivity.getIntent(this))
        finish()
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}
