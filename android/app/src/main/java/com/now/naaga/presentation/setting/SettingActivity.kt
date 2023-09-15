package com.now.naaga.presentation.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.databinding.ActivitySettingBinding
import com.now.naaga.presentation.login.LoginActivity

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
            viewModel.withdrawalMember()
        }
    }

    private fun subscribe() {
        viewModel.withdrawalStatue.observe(this) { status ->
            if (status == true) {
                shortToast(WITHDRAWAL_SUCCESS_MESSAGE)
                navigateLogin()
            }
        }

        viewModel.errorMessage.observe(this) { error: DataThrowable ->
            when (error.code) {
                101 -> shortToast(WRONG_ERROR_MESSAGE)
                102 -> shortToast(EXPIRATION_ERROR_MESSAGE)
            }
        }
    }

    private fun navigateLogin() {
        startActivity(LoginActivity.getIntent(this))
        finish()
    }

    private fun shortToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    companion object {
        private const val WITHDRAWAL_SUCCESS_MESSAGE = "성공적으로 회원 탈퇴 되었습니다."
        private const val WRONG_ERROR_MESSAGE = "인증 정보가 잘 못 되었어요!"
        private const val EXPIRATION_ERROR_MESSAGE = "인증 정보가 만료 되었어요!"

    fun getIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }
}
