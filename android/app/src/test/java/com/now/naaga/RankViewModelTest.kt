package com.now.naaga

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.now.domain.model.OrderType
import com.now.domain.model.Player
import com.now.domain.model.Rank
import com.now.domain.model.SortType
import com.now.domain.repository.RankRepository
import com.now.naaga.presentation.rank.RankViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RankViewModelTest {
    private lateinit var vm: RankViewModel
    private lateinit var rankRepository: RankRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val fakeMyLank = Rank(
        Player(
            id = 1,
            nickname = "뽀또",
            score = 1000,
        ),
        rank = 1,
        percent = 1,
    )

    private val fakeRanksList = listOf(
        Rank(
            player = Player(
                id = 1,
                nickname = "뽀또",
                score = 1000,
            ),
            rank = 1,
            percent = 1,
        ),
        Rank(
            player = Player(
                id = 2,
                nickname = "뽀또2",
                score = 1100,
            ),
            rank = 2,
            percent = 2,
        ),
        Rank(
            player = Player(
                id = 3,
                nickname = "뽀또3",
                score = 1200,
            ),
            rank = 3,
            percent = 3,
        ),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        rankRepository = mockk()
        vm = RankViewModel(rankRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `내 랭킹 조회`() {
        // given
        coEvery {
            rankRepository.getMyRank()
        } coAnswers {
            fakeMyLank
        }

        // when
        vm.fetchMyRank()

        // then
        assertEquals(vm.myRank.getOrAwaitValue(), fakeMyLank.rank)
        assertEquals(vm.myName.getOrAwaitValue(), fakeMyLank.player.nickname)
        assertEquals(vm.myScore.getOrAwaitValue(), fakeMyLank.player.score)
    }

    @Test
    fun `전체 랭킹 조회`() {
        // given
        coEvery {
            rankRepository.getAllRanks(SortType.RANK.name, OrderType.ASCENDING.name)
        } coAnswers {
            fakeRanksList
        }

        // when
        vm.fetchRanks()

        // then
        assertEquals(vm.ranks.getOrAwaitValue(), fakeRanksList)
    }
}
