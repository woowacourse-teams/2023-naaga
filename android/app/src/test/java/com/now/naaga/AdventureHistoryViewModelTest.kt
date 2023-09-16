package com.now.naaga

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.now.domain.model.AdventureResult
import com.now.domain.model.AdventureResultType
import com.now.domain.model.Coordinate
import com.now.domain.model.OrderType
import com.now.domain.model.Place
import com.now.domain.model.SortType
import com.now.domain.repository.AdventureRepository
import com.now.naaga.presentation.adventurehistory.AdventureHistoryViewModel
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

class AdventureHistoryViewModelTest {
    private lateinit var vm: AdventureHistoryViewModel
    private lateinit var adventureRepository: AdventureRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val fakeAdventureResults = listOf(
        AdventureResult(
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
        ),
        AdventureResult(
            id = 2,
            gameId = 1,
            destination = Place(
                id = 1,
                name = "파이브 가이즈",
                coordinate = Coordinate(37.1234, 125.1234),
                image = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fb" +
                    "log.kakaocdn.net%2Fdn%2FcPs9Im%2Fbtrb2k1feQq%2FBW34tbqjtHscUYkgCPyjcK%2Fimg.jpg,",
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
        ),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        adventureRepository = mockk()
        vm = AdventureHistoryViewModel(adventureRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `내 게임 결과 리스트 불러오기`() {
        // given
        coEvery {
            adventureRepository.fetchMyAdventureResults(SortType.TIME, OrderType.DESCENDING)
        } coAnswers {
            fakeAdventureResults
        }

        // when
        vm.fetchHistories()

        // then
        assertEquals(vm.adventureResults.getOrAwaitValue(), fakeAdventureResults)
    }
}
