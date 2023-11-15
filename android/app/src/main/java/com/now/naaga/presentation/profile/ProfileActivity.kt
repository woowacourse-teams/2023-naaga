package com.now.naaga.presentation.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.now.naaga.R
import com.now.naaga.databinding.ActivityProfileBinding
import com.now.naaga.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        subscribe()
        setClickListeners()
    }

    private fun subscribe() {
        viewModel.modifyStatus.observe(this) { status ->
            if (status) {
                showToast(getString(R.string.profile_modify_success_message))
                finish()
            }
        }
        viewModel.throwable.observe(this) {
            showToast(getString(R.string.profile_modify_fail_message))
        }
    }

    private fun setClickListeners() {
        binding.ivProfileBack.setOnClickListener {
            finish()
        }
        binding.btnProfileNicknameModify.setOnClickListener {
            if (viewModel.isFormValid()) {
                viewModel.modifyNickname()
            } else {
                showToast(getString(R.string.profile_no_content_message))
            }
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }
}
