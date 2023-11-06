package com.now.naaga

import android.text.Editable
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.now.domain.model.Coordinate
import com.now.domain.model.Place
import com.now.domain.repository.PlaceRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.presentation.upload.UploadStatus
import com.now.naaga.presentation.upload.UploadViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class UploadViewModelTest {
    private lateinit var viewModel: UploadViewModel
    private lateinit var placeRepository: PlaceRepository

    // 라이브데이터가 메인 스레드에서 동작하도록 함
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        placeRepository = mockk()
        viewModel = UploadViewModel(placeRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun String.toEditable(): Editable {
        return Editable.Factory.getInstance().newEditable(this)
    }

    @Test
    fun `Coordinate이 입력되면 뷰모델의 Coordinate도 바뀐다`() {
        // given
        val coordinate = Coordinate(123.4567, 37.890)

        // when
        viewModel.setCoordinate(coordinate)

        // then
        assertEquals(Coordinate(123.4567, 37.890), viewModel.coordinate.value)
    }

    @Test
    fun `데이터 전송 성공시 successUpload가 UploadStatus SUCCESS다`() {
        // given
        val coordinate = Coordinate(123.4567, 37.890)
        val emptyFile = File("")
        viewModel.setCoordinate(coordinate)
        viewModel.setFile(emptyFile)

        coEvery {
            placeRepository.postPlace(any(), any(), any(), any())
        } returns (
            Place(
                id = 1,
                name = "krrong",
                coordinate = Coordinate(37.1234, 127.1234),
                image = "https://img.segye.com/content/image/2021/07/29/20210729513138.jpg",
                description = "android",
            )
            )

        // when
        viewModel.postPlace()

        // Assert: 결과 확인
        assertEquals(UploadStatus.SUCCESS, viewModel.successUpload.getValue())
    }

    @Test
    fun `데이터 전송 실패시 successUpload가 UploadStatus Fail이고 반환된 throwable이 저장된다`() {
        // given
        val coordinate = Coordinate(123.4567, 37.890)
        val emptyFile = File("")
        viewModel.setCoordinate(coordinate)
        viewModel.setFile(emptyFile)
        val placeThrowable = DataThrowable.PlaceThrowable(505, "Test Failure")
        coEvery { placeRepository.postPlace(any(), any(), any(), any()) } throws placeThrowable

        // when
        runBlocking { viewModel.postPlace() }

        // then
        // placeRepository.postPlace가 실행되었는지 확인
        coVerify { placeRepository.postPlace(any(), any(), any(), any()) }

        // successUpload가 UploadStatus.Fail 인지 확인
        assertEquals(UploadStatus.FAIL, viewModel.successUpload.getValue())

        // 의도한 throwable이 저장되었는지 확인
        assertEquals(placeThrowable, viewModel.throwable.value)
    }
}
