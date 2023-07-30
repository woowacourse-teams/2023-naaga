package com.now.naaga.presentation.adventureresult

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.now.naaga.databinding.ActivityAdventureResultBinding

class AdventureResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdventureResultBinding
    private lateinit var viewModel: AdventureResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdventureResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        viewModel.checkAdventureResultState(AdventureResultViewModel.TEMP_SUCCESS_CODE)
        binding.viewModel = viewModel
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[AdventureResultViewModel::class.java]
    }
}
