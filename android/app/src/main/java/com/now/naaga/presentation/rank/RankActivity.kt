package com.now.naaga.presentation.rank

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.now.naaga.databinding.ActivityRankBinding

class RankActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRankBinding
    private lateinit var viewModel: RankViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[RankViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }
}
