package com.now.naaga.presentation.onadventure

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.now.naaga.R

class OnAdventureActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapFragment
    private lateinit var naverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_adventure)

        setMapView()
    }

    private fun setMapView() {
        val fragmentManager = supportFragmentManager
        val mapFragment = fragmentManager.findFragmentById(R.id.fcv_main_map) as MapFragment
        mapFragment.getMapAsync(this)
        mapView = mapFragment
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        setCameraPosition()
    }

    private fun setCameraPosition() {
        val cameraPosition = CameraPosition(
            TEMP_CAMERA_POSITION,
            ZOOM_LEVEL,
        )
        naverMap.cameraPosition = cameraPosition
    }

    companion object {
        private val TEMP_CAMERA_POSITION = LatLng(37.515304, 127.103078)
        private const val ZOOM_LEVEL = 16.0
    }
}
