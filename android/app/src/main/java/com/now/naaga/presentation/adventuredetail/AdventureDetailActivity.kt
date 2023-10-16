package com.now.naaga.presentation.adventuredetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.now.naaga.databinding.ActivityAdventureDetailBinding
import com.now.naaga.presentation.adventuredetail.viewpager.ViewPagerAdapter
import com.now.naaga.presentation.uimodel.model.OpenLetterUiModel

class AdventureDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdventureDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdventureDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewPager()
    }

    private fun initViewPager() {
        binding.vpAdventureDetail.adapter = ViewPagerAdapter(
            listOf(
                List(10) { OpenLetterUiModel("krrong", "today", "meesage") },
                List(10) { OpenLetterUiModel("krrong", "today", "meesage") },
            ),
        )

        TabLayoutMediator(binding.tlAdventureDetail, binding.vpAdventureDetail) { tab, position ->
            when (position) {
                0 -> tab.text = "읽은 편지"
                1 -> tab.text = "등록한 편지"
            }
        }.attach()
    }
}
