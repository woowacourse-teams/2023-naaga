package com.now.naaga.presentation.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.now.naaga.R
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.databinding.ActivitySettingBinding
import com.now.naaga.presentation.login.LoginActivity
import com.now.naaga.presentation.onadventure.NaagaAlertDialog

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var viewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        setClickListeners()
        subscribe()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, SettingViewModel.Factory)[SettingViewModel::class.java]
        binding.lifecycleOwner = this
    }

    private fun setClickListeners() {
        binding.ivSettingBack.setOnClickListener {
            finish()
        }

        binding.tvSettingUnlink.setOnClickListener {
            showWithdrawalDialog()
        }
    }

    private fun subscribe() {
        viewModel.withdrawalStatus.observe(this) { status ->
            if (status == true) {
                shortToast(getString(R.string.setting_withdrawal_success_message))
                navigateLogin()
            }
        }

        viewModel.errorMessage.observe(this) { error: DataThrowable ->
            when (error.code) {
                WRONG_AUTH_ERROR_CODE -> shortToast(getString(R.string.setting_wrong_error_message))
                EXPIRATION_AUTH_ERROR_CODE -> shortToast(getString(R.string.setting_expiration_error_message))
            }
        }
    }

    private fun navigateLogin() {
        startActivity(LoginActivity.getIntentWithTop(this))
        finish()
    }

    private fun shortToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showWithdrawalDialog() {
        NaagaAlertDialog.Builder().build(
            title = getString(R.string.withdrawal_dialog_title),
            description = getString(R.string.withdrawal_dialog_description),
            positiveText = getString(R.string.withdrawal_dialog_negative),
            negativeText = getString(R.string.withdrawal_dialog_positive),
            positiveAction = { },
            negativeAction = { viewModel.withdrawalMember() },
        ).show(supportFragmentManager, WITHDRAWAL)
    }

    companion object {
        private const val WITHDRAWAL = "WITHDRAWAL"

        private const val WRONG_AUTH_ERROR_CODE = 101
        private const val EXPIRATION_AUTH_ERROR_CODE = 102

        fun getIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }
}
