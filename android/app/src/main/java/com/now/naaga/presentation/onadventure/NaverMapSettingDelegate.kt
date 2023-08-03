package com.now.naaga.presentation.onadventure

import android.graphics.PointF
import android.view.Gravity
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationSource
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.CompassView
import com.naver.maps.map.widget.LocationButtonView
import com.now.domain.model.Coordinate
import com.now.domain.model.Direction
import com.now.domain.model.Hint
import com.now.naaga.R
import com.now.naaga.util.dpToPx

interface NaverMapSettingDelegate : OnMapReadyCallback {
    val mapFragment: MapFragment
    val naverMap: NaverMap
    val locationSource: LocationSource
    val hintMarkers: MutableList<Marker>

    fun setNaverMap(activity: AppCompatActivity, @IdRes mapLayoutId: Int)
    fun addHintMarker(hint: Hint)
    fun addDestinationMarker(coordinate: Coordinate)
    fun setOnMapReady(action: () -> Unit)
}

class DefaultNaverMapSettingDelegate() : NaverMapSettingDelegate, DefaultLifecycleObserver {
    private lateinit var activity: AppCompatActivity
    private var mapLayoutId: Int? = null
    private lateinit var action: () -> Unit

    override lateinit var mapFragment: MapFragment // 지도에 대한 뷰 역할을 담당
    override lateinit var naverMap: NaverMap // API를 호출하기 위한 인터페이스
    override lateinit var locationSource: LocationSource // 네이버 지도 SDK에 위치를 제공하는 인터페이스
    override val hintMarkers: MutableList<Marker> = mutableListOf()

    override fun setNaverMap(activity: AppCompatActivity, @IdRes mapLayoutId: Int) {
        this.activity = activity
        this.activity.lifecycle.addObserver(this)
        this.mapLayoutId = mapLayoutId
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        setMapView()
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

    private fun setLocationSource() {
        locationSource = FusedLocationSource(activity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        setLocationSource()
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        setNaverMapButtons()
        action()
    }

    private fun setNaverMapButtons() {
        with(naverMap.uiSettings) {
            isCompassEnabled = false
            isScaleBarEnabled = true
            isLocationButtonEnabled = false
            logoGravity = Gravity.END
            setLogoMargin(0, dpToPx(activity, 8), 0, 0)
        }
        mapFragment.getMapAsync {
            val locationButton = activity.findViewById<LocationButtonView>(R.id.btn_onAdventure_navermap_location)
            val compassView = activity.findViewById<CompassView>(R.id.navermap_compass_onAdventure)
            locationButton.map = naverMap
            compassView.map = naverMap
        }
    }

    override fun addHintMarker(hint: Hint) {
        val marker = Marker().apply {
            position = LatLng(hint.coordinate.latitude, hint.coordinate.longitude)
            icon = when (hint.direction) {
                Direction.NORTH -> OverlayImage.fromResource(R.drawable.ic_marker_north)
                Direction.SOUTH -> OverlayImage.fromResource(R.drawable.ic_marker_south)
                Direction.EAST -> OverlayImage.fromResource(R.drawable.ic_marker_east)
                Direction.WEST -> OverlayImage.fromResource(R.drawable.ic_marker_west)
                Direction.NONE -> return
            }
            anchor = PointF(0.5f, 1f)
            map = naverMap
        }
        hintMarkers.add(marker)
    }

    override fun addDestinationMarker(coordinate: Coordinate) {
        Marker().apply {
            position = LatLng(coordinate.latitude, coordinate.longitude)
            map = naverMap
        }
    }

    override fun setOnMapReady(action: () -> Unit) {
        this.action = action
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
