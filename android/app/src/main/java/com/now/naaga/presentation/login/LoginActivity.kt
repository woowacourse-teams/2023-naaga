package com.now.naaga.presentation.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.now.domain.model.Adventure
import com.now.domain.model.AuthPlatformType
import com.now.naaga.NaagaApplication
import com.now.naaga.R
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.firebase.analytics.LOGIN_AUTH
import com.now.naaga.data.repository.DefaultAuthRepository
import com.now.naaga.databinding.ActivityLoginBinding
import com.now.naaga.presentation.beginadventure.BeginAdventureActivity
import com.now.naaga.presentation.uimodel.mapper.toDomain
import com.now.naaga.presentation.uimodel.mapper.toUi
import com.now.naaga.presentation.uimodel.model.AdventureUiModel
import com.now.naaga.util.getParcelableCompat
import com.now.naaga.util.loginWithKakao

class LoginActivity : AppCompatActivity(), AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        registerAnalytics(this.lifecycle)
        initViewModel()
        subscribe()
        setClickListeners()
        setStatusBar()
    }

    private fun initViewModel() {
        val factory = LoginViewModelFactory(DefaultAuthRepository(NaagaApplication.authDataSource))
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private fun subscribe() {
        viewModel.isLoginSucceed.observe(this) { isSucceed ->
            if (isSucceed) {
                navigateHome()
            }
        }

        viewModel.throwable.observe(this) { throwable ->
            Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
            logServerError(LOGIN_AUTH, throwable.code, throwable.message.toString())
        }
    }

    private fun setClickListeners() {
        binding.ivLoginKakao.setOnClickListener {
            loginWithKakao(this) { viewModel.signIn(it, AuthPlatformType.KAKAO) }
        }
    }

    private fun setStatusBar() {
        window.apply {
            statusBarColor = getColor(R.color.white)
            WindowInsetsControllerCompat(this, this.decorView).isAppearanceLightStatusBars = true
        }
    }

    private fun navigateHome() {
        val adventure = intent.getParcelableCompat(ADVENTURE, AdventureUiModel::class.java)?.toDomain()

        val intent = if (adventure == null) {
            BeginAdventureActivity.getIntent(this)
        } else {
            BeginAdventureActivity.getIntentWithAdventure(this, adventure)
        }

        startActivity(intent)
        finish()
    }

    companion object {
        private const val ADVENTURE = "ADVENTURE"

        fun getIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }

        fun getIntentWithAdventure(context: Context, adventure: Adventure): Intent {
            return Intent(context, LoginActivity::class.java).apply {
                putExtra(ADVENTURE, adventure.toUi())
            }
        }
    }
}
