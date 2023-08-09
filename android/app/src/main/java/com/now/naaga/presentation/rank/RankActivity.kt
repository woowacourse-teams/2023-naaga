package com.now.naaga.presentation.rank

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.now.domain.model.Rank
import com.now.naaga.data.repository.DefaultRankRepository
import com.now.naaga.databinding.ActivityRankBinding
import com.now.naaga.presentation.rank.recyclerview.RankAdapter

class RankActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRankBinding
    private lateinit var viewModel: RankViewModel

    private val rankAdapter = RankAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        initRecyclerView()
        viewModel.fetchMyRank()
        viewModel.fetchRanks()
        subscribe()
        setClickListeners()
    }

    private fun initViewModel() {
        val repository = DefaultRankRepository()
        val factory = RankFactory(repository)
        viewModel = ViewModelProvider(this, factory)[RankViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initRecyclerView() {
        binding.rvRankWholeRanks.apply {
            adapter = rankAdapter
            setHasFixedSize(true)
        }
    }

    private fun subscribe() {
        viewModel.ranks.observe(this) { ranks ->
            updateRank(ranks)
        }
        viewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateRank(places: List<Rank>) {
        rankAdapter.submitList(places)
    }

    private fun setClickListeners() {
        binding.ivRankClose.setOnClickListener {
            finish()
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, RankActivity::class.java)
        }
    }
}
