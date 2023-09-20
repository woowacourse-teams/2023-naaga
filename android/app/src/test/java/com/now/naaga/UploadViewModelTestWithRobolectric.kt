package com.now.naaga

import android.widget.EditText
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.google.firebase.FirebaseApp
import com.now.domain.repository.PlaceRepository
import com.now.naaga.presentation.upload.UploadActivity
import com.now.naaga.presentation.upload.UploadViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
class UploadViewModelTestWithRobolectric {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var placeRepository: PlaceRepository

    @Before
    fun setup() {
        hiltRule.inject()
        FirebaseApp.initializeApp(getApplicationContext())
    }

    @Test
    fun `editText 값이 변경되면 뷰모델의 name 프로퍼티도 변경된다`() {
        // given
        val activity = Robolectric
            .buildActivity(UploadActivity::class.java)
            .create()
            .visible()
            .get()

        // EditText 찾기
        val editTextTitle = activity.findViewById<EditText>(R.id.et_upload_photo_title)

        // when : EditText 값 변경
        val testInput = "Test Input"
        editTextTitle.setText(testInput)

        // ViewModelProvider에서 뷰모델 가져오기
        val viewModel = ViewModelProvider(activity)[UploadViewModel::class.java]

        // LiveData 값 확인
        val nameLiveData = viewModel.name
        val observedValue = nameLiveData.getOrAwaitValue()

        // then : EditText 값과 LiveData 값이 일치하는지 확인
        assertEquals(testInput, observedValue)
    }
}
