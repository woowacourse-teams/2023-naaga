package com.now.naaga.presentation.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.now.naaga.R
import com.now.naaga.databinding.ActivitySettingBinding
import com.now.naaga.presentation.onadventure.NaagaAlertDialog

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var viewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        subscribe()
        setClickListeners()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, SettingViewModel.Factory)[SettingViewModel::class.java]
        binding.lifecycleOwner = this
    }

    private fun subscribe() {
        viewModel.isLoggedIn.observe(this) { isLoggedIn ->
            if (!isLoggedIn) {
                shortToast(getString(R.string.setting_logout_message))
                finish()
            }
        }
        viewModel.throwable.observe(this) {
            shortToast(it.message.toString())
        }
    }

    private fun shortToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setClickListeners() {
        binding.tvSettingLogout.setOnClickListener {
            makeLogoutDialog().show(supportFragmentManager, LOGOUT)
        }
        binding.ivSettingBack.setOnClickListener {
            finish()
        }
    }

    private fun makeLogoutDialog(): DialogFragment {
        return NaagaAlertDialog.Builder().build(
            title = getString(R.string.logout_dialog_title),
            description = getString(R.string.logout_dialog_description),
            positiveText = getString(R.string.logout_dialog_positive_text),
            negativeText = getString(R.string.logout_dialog_negative_text),
            positiveAction = { },
            negativeAction = { viewModel.logout() },
        )
    }

    companion object {
        private const val LOGOUT = "LOGOUT"

        fun getIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }
}
