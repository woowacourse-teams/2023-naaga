package com.now.naaga.presentation.login

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.now.domain.model.type.AuthPlatformType
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.databinding.ActivityLoginBinding
import com.now.naaga.presentation.beginadventure.BeginAdventureActivity
import com.now.naaga.util.extension.showToast
import com.now.naaga.util.loginWithKakao
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        registerAnalytics(this.lifecycle)
        subscribe()
        setClickListeners()
    }

    private fun subscribe() {
        viewModel.isLoginSucceed.observe(this) { isSucceed ->
            if (isSucceed) {
                navigateHome()
            }
        }

        viewModel.throwable.observe(this) { throwable: DataThrowable ->
            when (throwable.code) {
                DataThrowable.NETWORK_THROWABLE_CODE -> { showToast(throwable.message ?: "") }
            }
        }
    }

    private fun setClickListeners() {
        binding.ivLoginKakao.setOnClickListener {
            loginWithKakao(this) { viewModel.signIn(it, AuthPlatformType.KAKAO) }
        }
    }

    private fun navigateHome() {
        val intent = BeginAdventureActivity.getIntent(this)
        startActivity(intent)
        finish()
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }

        fun getIntentWithTop(context: Context): Intent {
            return Intent(context, LoginActivity::class.java).apply {
                addFlags(FLAG_ACTIVITY_NEW_TASK)
                addFlags(FLAG_ACTIVITY_CLEAR_TASK)
            }
        }
    }
}
