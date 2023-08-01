package com.now.naaga.presentation.onadventure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.naver.maps.map.LocationSource
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.now.domain.model.Coordinate
import com.now.domain.model.Game
import com.now.naaga.R
import com.now.naaga.databinding.ActivityOnAdventureBinding
import com.now.naaga.presentation.uimodel.mapper.toDomain
import com.now.naaga.presentation.uimodel.mapper.toUi
import com.now.naaga.presentation.uimodel.model.GameUiModel
import com.now.naaga.util.getParcelable

class OnAdventureActivity2 : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityOnAdventureBinding
    private lateinit var viewModel: OnAdventureViewModel2

    private lateinit var mapFragment: MapFragment // 지도에 대한 뷰 역할을 담당
    private lateinit var naverMap: NaverMap // API를 호출하기 위한 인터페이스
    private lateinit var locationSource: LocationSource // 네이버 지도 SDK에 위치를 제공하는 인터페이스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnAdventureBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, OnAdventureViewModel2.Factory)[OnAdventureViewModel2::class.java]
        setContentView(binding.root)

        setMapView()
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun setMapView() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.fcv_onAdventure_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fcv_onAdventure_map, it)
                    .commit()
            }
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        setNaverMapButtons()
        // beginAdventure()
    }

    private fun setNaverMapButtons() {
        with(naverMap.uiSettings) {
            isCompassEnabled = true
            isScaleBarEnabled = true
            isLocationButtonEnabled = true
        }
    }

    fun beginAdventure(coordinate: Coordinate) {
        val existingAdventure = intent.getParcelable(ADVENTURE, GameUiModel::class.java)?.toDomain()

        if (existingAdventure == null) {
            viewModel.beginAdventure(coordinate)
            return
        }
        viewModel.setAdventure(existingAdventure)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val DESTINATION_PHOTO = "DESTINATION_PHOTO"
        private const val ADVENTURE = "ADVENTURE"

        fun getIntent(context: Context): Intent {
            return Intent(context, OnAdventureActivity::class.java)
        }

        fun getIntentWithAdventure(context: Context, adventure: Game): Intent {
            return Intent(context, OnAdventureActivity::class.java).apply {
                putExtra(ADVENTURE, adventure.toUi())
            }
        }
    }
}
