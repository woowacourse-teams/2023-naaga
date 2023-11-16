package com.now.naaga.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.now.naaga.R
import com.now.naaga.data.firebase.analytics.AnalyticsDelegate
import com.now.naaga.data.firebase.analytics.DefaultAnalyticsDelegate
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.presentation.beginadventure.BeginAdventureActivity
import com.now.naaga.presentation.common.dialog.NaagaAlertDialog
import com.now.naaga.presentation.login.LoginActivity
import com.now.naaga.presentation.splash.SplashViewModel.Companion.EXPIRATION_AUTH_ERROR_CODE
import com.now.naaga.util.extension.getPackageInfoCompat
import com.now.naaga.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity(), AnalyticsDelegate by DefaultAnalyticsDelegate() {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerAnalytics(this.lifecycle)
        updateCheck()
        subscribe()
        setContentView(R.layout.activity_splash)
    }

    private fun updateCheck() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mapOf(MIN_VERSION to DEFAULT_VERSION))
        val curVersion = packageManager.getPackageInfoCompat(packageName).versionName

        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                val minVersion = remoteConfig.getString(MIN_VERSION)
                if (minVersion > curVersion) {
                    showUpdateDialog()
                } else {
                    viewModel.testTokenValid()
                }
            }
        }
    }

    private fun showUpdateDialog() {
        NaagaAlertDialog.Builder()
            .setCancelable(false)
            .build(
                title = getString(R.string.confirm_dialog_title),
                description = getString(R.string.confirm_dialog_description),
                positiveText = getString(R.string.confirm_dialog_positive_text),
                negativeText = getString(R.string.confirm_dialog_negative_text),
                positiveAction = ::navigateToPlayStore,
                negativeAction = ::finish,
            ).show(supportFragmentManager, TAG_CONFIRM_DIALOG)
    }

    private fun navigateToPlayStore() {
        val uri = PLAY_STORE_URI + packageName
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
        finish()
    }

    private fun subscribe() {
        viewModel.isTokenValid.observe(this) { isTokenValid: Boolean ->
            if (isTokenValid) {
                startBeginAdventureActivity()
                return@observe
            }
            startLoginActivity()
        }
        viewModel.throwable.observe(this) { throwable: DataThrowable ->
            when (throwable.code) {
                DataThrowable.NETWORK_THROWABLE_CODE -> showToast(getString(R.string.network_error_message))
                EXPIRATION_AUTH_ERROR_CODE -> showToast(getString(R.string.splash_re_login_message))
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

    companion object {
        const val TAG_CONFIRM_DIALOG = "CONFIRM"
        const val MIN_VERSION = "version"
        const val PLAY_STORE_URI = "market://details?id="
        const val DEFAULT_VERSION = "0.0.0"
    }
}
