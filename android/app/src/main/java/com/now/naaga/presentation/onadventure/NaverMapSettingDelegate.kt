package com.now.naaga.presentation.onadventure

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.naver.maps.map.LocationSource
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource

interface NaverMapSettingDelegate {
    val mapFragment: MapFragment
    val naverMap: NaverMap
    val locationSource: LocationSource

    fun setNaverMap(activity: AppCompatActivity, @IdRes mapLayoutId: Int)
}

class DefaultNaverMapSettingDelegate() : NaverMapSettingDelegate, DefaultLifecycleObserver, OnMapReadyCallback {
    private lateinit var activity: AppCompatActivity
    private var mapLayoutId: Int? = null

    override lateinit var mapFragment: MapFragment
    override lateinit var naverMap: NaverMap
    override lateinit var locationSource: LocationSource

    override fun setNaverMap(activity: AppCompatActivity, @IdRes mapLayoutId: Int) {
        this.activity = activity
        this.mapLayoutId = mapLayoutId
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        setMapView()
        locationSource = FusedLocationSource(activity, LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun setMapView() {
        if (mapLayoutId == null) return
        val fm = activity.supportFragmentManager

        mapFragment = fm.findFragmentById(mapLayoutId!!) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction()
                    .add(mapLayoutId!!, it)
                    .commit()
            }
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        setNaverMapButtons()
    }

    private fun setNaverMapButtons() {
        with(naverMap.uiSettings) {
            isCompassEnabled = true
            isScaleBarEnabled = true
            isLocationButtonEnabled = true
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
