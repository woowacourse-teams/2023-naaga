package com.now.naaga.presentation.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.now.naaga.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }
}
