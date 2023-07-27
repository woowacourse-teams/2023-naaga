package com.now.naaga.presentation.upload

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.now.naaga.R

class UploadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, UploadActivity::class.java)
        }
    }
}
