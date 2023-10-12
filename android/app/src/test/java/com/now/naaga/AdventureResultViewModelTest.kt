package com.now.naaga

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.now.domain.model.AdventureResult
import com.now.domain.model.Coordinate
import com.now.domain.model.Place
import com.now.domain.model.Player
import com.now.domain.model.Rank
import com.now.domain.model.type.AdventureResultType
import com.now.domain.repository.AdventureRepository
import com.now.domain.repository.RankRepository
import com.now.naaga.presentation.adventureresult.AdventureResultViewModel
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class AdventureResultViewModelTest {
    private lateinit var vm: AdventureResultViewModel
    private lateinit var adventureRepository: AdventureRepository
    private lateinit var rankRepository: RankRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val fakeAdventureResultOnSuccess = AdventureResult(
        id = 1,
        gameId = 1,
        destination = Place(
            id = 1,
            name = "파이브 가이즈",
            coordinate = Coordinate(37.1234, 125.1234),
            image = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn" +
                ".net%2Fdn%2FcPs9Im%2Fbtrb2k1feQq%2FBW34tbqjtHscUYkgCPyjcK%2Fimg.jpg,",
            description = "룰루랄라",
        ),
        resultType = AdventureResultType.SUCCESS,
        score = 1000,
        playTime = 30,
        distance = 200,
        hintUses = 3,
        tryCount = 2,
        beginTime = LocalDateTime.of(
            LocalDate.of(2023, 9, 16),
            LocalTime.of(13, 30),
        ),
        endTime = LocalDateTime.of(
            LocalDate.of(2023, 9, 16),
            LocalTime.of(14, 0),
        ),
    )

    private val fakeAdventureResultOnFailure = AdventureResult(
        id = 2,
        gameId = 1,
        destination = Place(
            id = 1,
            name = "파이브 가이즈",
            coordinate = Coordinate(37.1234, 125.1234),
            image = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net" +
                "%2Fdn%2FcPs9Im%2Fbtrb2k1feQq%2FBW34tbqjtHscUYkgCPyjcK%2Fimg.jpg,",
            description = "룰루랄라",
        ),
        resultType = AdventureResultType.FAIL,
        score = 1000,
        playTime = 30,
        distance = 200,
        hintUses = 3,
        tryCount = 2,
        beginTime = LocalDateTime.of(
            LocalDate.of(2023, 9, 16),
            LocalTime.of(13, 30),
        ),
        endTime = LocalDateTime.of(
            LocalDate.of(2023, 9, 16),
            LocalTime.of(14, 0),
        ),
    )

    private val fakeMyRank = Rank(
        player = Player(
            id = 1,
            nickname = "뽀또",
            score = 1000,
        ),
        rank = 1,
        percent = 1,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        adventureRepository = mockk()
        rankRepository = mockk()
        vm = AdventureResultViewModel(adventureRepository, rankRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `성공한 게임 결과 불러오기`() {
        // given
        coEvery {
            adventureRepository.fetchAdventureResult(1)
        } coAnswers {
            fakeAdventureResultOnSuccess
        }

        // when
        vm.fetchGameResult(1)

        // then
        assertEquals(vm.adventureResult.getOrAwaitValue(), fakeAdventureResultOnSuccess)
        assertEquals(vm.adventureResult.getOrAwaitValue().resultType, AdventureResultType.SUCCESS)
    }

    @Test
    fun `실패한 게임 결과 불러오기`() {
        // given
        coEvery {
            adventureRepository.fetchAdventureResult(2)
        } coAnswers {
            fakeAdventureResultOnFailure
        }

        // when
        vm.fetchGameResult(2)

        // then
        assertEquals(vm.adventureResult.getOrAwaitValue(), fakeAdventureResultOnFailure)
        assertEquals(vm.adventureResult.getOrAwaitValue().resultType, AdventureResultType.FAIL)
    }

    @Test
    fun `내 랭킹 결과 불러오기`() {
        // given
        coEvery {
            rankRepository.getMyRank()
        } coAnswers {
            fakeMyRank
        }

        // when
        vm.fetchMyRank()

        // then
        assertEquals(vm.myRank.getOrAwaitValue(), fakeMyRank.rank)
    }
}
