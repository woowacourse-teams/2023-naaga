package com.now.naaga.presentation.onadventure

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.now.domain.model.Coordinate
import com.now.naaga.R
import com.now.naaga.data.repository.MockDestinationRepository
import com.now.naaga.databinding.ActivityOnAdventureBinding
import com.now.naaga.presentation.beginadventure.DestinationPhotoDialog

class OnAdventureActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityOnAdventureBinding
    private lateinit var viewModel: OnAdventureViewModel

    private lateinit var mapView: MapFragment
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnAdventureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        setMapView()
        clickPhotoIcon()
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initViewModel() {
        val repository = MockDestinationRepository()
        val factory = OnAdventureFactory(repository)
        viewModel = ViewModelProvider(this, factory)[OnAdventureViewModel::class.java]
    }

    private fun setMapView() {
        val fragmentManager = supportFragmentManager
        val mapFragment = fragmentManager.findFragmentById(R.id.fcv_onAdventure_map) as MapFragment
        mapFragment.getMapAsync(this)
        mapView = mapFragment
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        enableLocationButton()
        setFollowMode()

        naverMap.addOnLocationChangeListener { location ->
            viewModel.calculateDistance(Coordinate(location.latitude, location.longitude))
        }
        addMarker(DESTINATION_COORDINATE)
    }

    private fun enableLocationButton() {
        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = true
    }

    private fun setFollowMode() {
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
    }

    private fun addMarker(coordinate: Coordinate) {
        Marker().apply {
            position = LatLng(coordinate.latitude, coordinate.longitude)
            map = naverMap
        }
    }

    private fun clickPhotoIcon() {
        binding.ivOnAdventurePhoto.setOnClickListener {
            val fragment: Fragment? = supportFragmentManager.findFragmentByTag(DESTINATION_PHOTO)
            if (fragment == null) {
                DestinationPhotoDialog.makeDialog("")
                    .show(supportFragmentManager, DESTINATION_PHOTO)
            } else {
                (fragment as DialogFragment).dialog?.show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val DESTINATION_LATITUDE = 37.514907
        private const val DESTINATION_LONGITUDE = 127.103198
        private val DESTINATION_COORDINATE = Coordinate(DESTINATION_LATITUDE, DESTINATION_LONGITUDE)
        private const val DESTINATION_PHOTO = "DESTINATION_PHOTO"
    }
}
