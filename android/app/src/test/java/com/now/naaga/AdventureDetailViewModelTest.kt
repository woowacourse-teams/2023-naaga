package com.now.naaga

import com.now.domain.model.AdventureResult
import com.now.domain.model.Coordinate
import com.now.domain.model.Place
import com.now.domain.model.Player
import com.now.domain.model.letter.Letter
import com.now.domain.model.type.AdventureResultType
import com.now.domain.model.type.LogType
import com.now.domain.repository.AdventureRepository
import com.now.domain.repository.LetterRepository
import com.now.naaga.presentation.adventuredetail.AdventureDetailUiState
import com.now.naaga.presentation.adventuredetail.AdventureDetailViewModel
import com.now.naaga.presentation.uimodel.mapper.toUiModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertSame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class AdventureDetailViewModelTest {
    private lateinit var vm: AdventureDetailViewModel
    private lateinit var letterRepository: LetterRepository
    private lateinit var adventureRepository: AdventureRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        letterRepository = mockk()
        adventureRepository = mockk()
        vm = AdventureDetailViewModel(letterRepository, adventureRepository)
    }

    @Test
    fun `읽은 쪽지만 불러오면 AdventureDetailUiState는 Loading 상태다`() {
        // given
        coEvery {
            letterRepository.fetchLetterLogs(1L, LogType.READ)
        } coAnswers {
            fakeReadLetterLogs
        }

        // when
        vm.fetchReadLetter(1L)

        // then
        assertSame(vm.uiState.value, AdventureDetailUiState.Loading)
    }

    @Test
    fun `작성한 쪽지만 불러오면 AdventureDetailUiState는 Loading 상태다`() {
        // given
        coEvery {
            letterRepository.fetchLetterLogs(1L, LogType.WRITE)
        } coAnswers {
            fakeWriteLetterLogs
        }

        // when
        vm.fetchWriteLetter(1L)

        // then
        assertSame(vm.uiState.value, AdventureDetailUiState.Loading)
    }

    @Test
    fun `읽은 쪽지와 작성한 쪽지를 모두 불러와도 AdventureDetailUiState은 Loading 상태다`() {
        // given
        coEvery {
            letterRepository.fetchLetterLogs(1L, LogType.WRITE)
        } coAnswers {
            fakeWriteLetterLogs
        }

        coEvery {
            letterRepository.fetchLetterLogs(1L, LogType.READ)
        } coAnswers {
            fakeReadLetterLogs
        }

        // when
        vm.fetchWriteLetter(1L)
        vm.fetchReadLetter(1L)

        // then
        assertSame(vm.uiState.value, AdventureDetailUiState.Loading)
    }

    @Test
    fun `읽은 쪽지, 작성한 쪽지, 게임 결과를 불러오면 AdventureDetailUiState은 Success 상태다`() {
        // given
        coEvery {
            letterRepository.fetchLetterLogs(1L, LogType.WRITE)
        } coAnswers {
            fakeWriteLetterLogs
        }

        coEvery {
            letterRepository.fetchLetterLogs(1L, LogType.READ)
        } coAnswers {
            fakeReadLetterLogs
        }

        coEvery {
            adventureRepository.fetchAdventureResult(1L)
        } coAnswers {
            fakeAdventureResult
        }

        // when
        vm.fetchWriteLetter(1L)
        vm.fetchReadLetter(1L)
        vm.fetchAdventureResult(1L)

        // then
        val actual = AdventureDetailUiState.Success(
            readLetters = fakeReadLetterLogs.map { it.toUiModel() },
            writeLetters = fakeWriteLetterLogs.map { it.toUiModel() },
            adventureResult = fakeAdventureResult,
        )
        assertEquals(vm.uiState.value, actual)
    }

    private val fakeReadLetterLogs = listOf(
        Letter(
            id = 1L,
            player = Player(1L, "krrong", 1234),
            coordinate = Coordinate(123.0, 123.0),
            message = "Hello im krrong",
            registerDate = "now",
        ),
    )

    private val fakeWriteLetterLogs = listOf(
        Letter(
            id = 1L,
            player = Player(1L, "notKrrong", 1212),
            coordinate = Coordinate(123.0, 123.0),
            message = "i was krrong",
            registerDate = "now",
        ),
    )

    private val fakeAdventureResult = AdventureResult(
        id = 1L,
        gameId = 2L,
        destination = Place(1L, "집", Coordinate(123.0, 37.0), "", ""),
        resultType = AdventureResultType.FAIL,
        score = 123,
        playTime = 123,
        distance = 123,
        hintUses = 123,
        tryCount = 1,
        beginTime = LocalDateTime.now(),
        endTime = LocalDateTime.now(),
    )
}
