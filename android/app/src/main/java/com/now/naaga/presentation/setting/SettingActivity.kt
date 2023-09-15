package com.now.naaga.presentation.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.now.naaga.R
import com.now.naaga.databinding.ActivitySettingBinding

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
            viewModel.logout()
        }
        binding.ivSettingBack.setOnClickListener {
            finish()
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }
}
