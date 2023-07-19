package com.now.naaga.presentation.beginadventure

import android.Manifest
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.now.naaga.R

class BeginAdventureActivity : AppCompatActivity() {

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (!isGranted) {
            // TODO : 설정페이지로 이동시키는 다이얼로그 생성
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_begin_adventure)

        locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}
