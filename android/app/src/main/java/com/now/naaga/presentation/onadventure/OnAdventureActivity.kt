package com.now.naaga.presentation.onadventure

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.Toast
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
import com.now.domain.model.Adventure
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Coordinate
import com.now.naaga.R
import com.now.naaga.data.repository.DefaultAdventureRepository
import com.now.naaga.databinding.ActivityOnAdventureBinding
import com.now.naaga.presentation.beginadventure.BeginAdventureActivity
import com.now.naaga.presentation.uimodel.mapper.toDomain
import com.now.naaga.presentation.uimodel.mapper.toUi
import com.now.naaga.presentation.uimodel.model.AdventureUiModel
import com.now.naaga.util.getParcelable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

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

        setMapView()
        initViewModel()
        clickPhotoIcon()
        startObserving()
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun getIntentData(): Adventure? {
        return intent.getParcelable(ADVENTURE, AdventureUiModel::class.java)?.toDomain()
    }

    private fun initViewModel() {
        val repository = DefaultAdventureRepository()
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
        addOnLocationChangeListener()
        beginAdventure()
    }

    private fun beginAdventure() {
        val adventure: Adventure? = getIntentData()
        if (adventure == null) {
            beginNewAdventure()
        } else {
            beginExistingAdventure(adventure)
        }
    }

    private fun beginExistingAdventure(adventure: Adventure) {
        viewModel.setAdventure(adventure)
    }

    private fun beginNewAdventure() {
        GlobalScope.async {
            val lastLocation: Location = getCurrentLocation(locationSource)
            viewModel.beginAdventure(Coordinate(lastLocation.latitude, lastLocation.longitude))
        }
    }

    private suspend fun getCurrentLocation(locationSource: FusedLocationSource): Location {
        delay(200L)
        return locationSource.lastLocation ?: getCurrentLocation(locationSource)
    }

    private fun addOnLocationChangeListener() {
        naverMap.addOnLocationChangeListener { location ->
            viewModel.calculateDistance(Coordinate(location.latitude, location.longitude))
            viewModel.checkArrived(Coordinate(location.latitude, location.longitude))
            setBinding(Coordinate(location.latitude, location.longitude))
        }
    }

    private fun enableLocationButton() {
        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = true
    }

    private fun setFollowMode() {
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
    }

    private fun setBinding(coordinate: Coordinate) {
        binding.currentMyCoordinate = coordinate
    }

    private fun startObserving() {
        viewModel.status.observe(this) { status ->
            stopAdventure(status)
            onStatusChanged(status)
        }
        viewModel.adventureId.observe(this) { adventureId ->
            viewModel.fetchDestination(adventureId)
        }
        viewModel.errorMessage.observe(this) { message ->
            // TODO
        }
    }

    private fun addMarker(coordinate: Coordinate) {
        Marker().apply {
            position = LatLng(coordinate.latitude, coordinate.longitude)
            map = naverMap
        }
    }

    private fun stopAdventure(status: AdventureStatus?) {
        if (status == AdventureStatus.NONE) {
            Toast.makeText(
                this,
                getString(R.string.onAdventure_fail_load_description),
                Toast.LENGTH_SHORT,
            ).show()
            finish()
        }
    }

    private fun clickPhotoIcon() {
        binding.ivOnAdventurePhoto.setOnClickListener {
            val image = viewModel.destination.value?.image
                ?: return@setOnClickListener Toast
                    .makeText(this, R.string.onAdventure_loading_photo, Toast.LENGTH_SHORT).show()
            val fragment: Fragment? = supportFragmentManager.findFragmentByTag(DESTINATION_PHOTO)
            if (fragment == null) {
                PolaroidDialog.makeDialog(image)
                    .show(supportFragmentManager, DESTINATION_PHOTO)
            } else {
                (fragment as DialogFragment).dialog?.show()
            }
        }
    }

    private fun onStatusChanged(status: AdventureStatus) {
        when (status) {
            AdventureStatus.DONE -> {
                Toast.makeText(this, getString(R.string.onAdventure_adventure_success), Toast.LENGTH_LONG).show()
                startActivity(Intent(this, BeginAdventureActivity::class.java))
                finish()
            }

            AdventureStatus.IN_PROGRESS ->
                Toast.makeText(this, getString(R.string.onAdventure_retry), Toast.LENGTH_LONG)
                    .show()

            AdventureStatus.NONE -> stopAdventure(status)
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val DESTINATION_PHOTO = "DESTINATION_PHOTO"
        private const val ADVENTURE = "ADVENTURE"

        fun getIntent(context: Context): Intent {
            return Intent(context, OnAdventureActivity::class.java)
        }

        fun getIntentWithAdventure(context: Context, adventure: Adventure): Intent {
            return Intent(context, OnAdventureActivity::class.java).apply {
                putExtra(ADVENTURE, adventure.toUi())
            }
        }
    }
}
