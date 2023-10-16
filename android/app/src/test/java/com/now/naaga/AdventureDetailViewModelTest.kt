package com.now.naaga

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.now.domain.model.Coordinate
import com.now.domain.model.Player
import com.now.domain.model.letter.OpenLetter
import com.now.domain.model.type.LogType
import com.now.domain.repository.LetterRepository
import com.now.naaga.presentation.adventuredetail.AdventureDetailViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AdventureDetailViewModelTest {
    private lateinit var vm: AdventureDetailViewModel
    private lateinit var letterRepository: LetterRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        letterRepository = mockk()
        vm = AdventureDetailViewModel(letterRepository)
    }

    @Test
    fun `읽은 쪽지를 불러올 때 작성한 쪽지는 불러오지 않는다`() {
        // given
        coEvery {
            letterRepository.fetchLetterLogs(1L, LogType.READ)
        } coAnswers {
            fakeReadLetterLogs
        }

        // when
        vm.fetchLetterLogs(1L, LogType.READ)

        // then
        assertTrue(vm.readLetters.isInitialized)
        assertEquals(vm.readLetters.getOrAwaitValue(), fakeReadLetterLogs)
        assertEquals(vm.writeLetters.isInitialized, false)
    }

    @Test
    fun `작성한 쪽지를 불러올 때 읽은 쪽지는 불러오지 않는다`() {
        // given
        coEvery {
            letterRepository.fetchLetterLogs(1L, LogType.WRITE)
        } coAnswers {
            fakeWriteLetterLogs
        }

        // when
        vm.fetchLetterLogs(1L, LogType.WRITE)

        // then
        assertEquals(vm.readLetters.isInitialized, false)
        assertTrue(vm.writeLetters.isInitialized)
        assertEquals(vm.writeLetters.getOrAwaitValue(), fakeWriteLetterLogs)
    }

    private val fakeReadLetterLogs = listOf(
        OpenLetter(
            id = 1L,
            player = Player(1L, "krrong", 1234),
            coordinate = Coordinate(123.0, 123.0),
            message = "Hello im krrong",
            registerDate = "now",
        ),
    )

    private val fakeWriteLetterLogs = listOf(
        OpenLetter(
            id = 1L,
            player = Player(1L, "notKrrong", 1212),
            coordinate = Coordinate(123.0, 123.0),
            message = "i was krrong",
            registerDate = "now",
        ),
    )
}
