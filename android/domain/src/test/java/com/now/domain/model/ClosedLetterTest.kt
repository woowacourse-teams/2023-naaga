package com.now.domain.model

import com.now.domain.model.letter.LetterPreview
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ClosedLetterTest {
    private lateinit var myCoordinate: Coordinate
    private lateinit var closeLetter: LetterPreview
    private lateinit var distantLetter: LetterPreview

    @BeforeEach
    fun setUp() {
        myCoordinate = Coordinate(37.549335, 127.075816)
        closeLetter = LetterPreview(1, Coordinate(37.549369, 127.076357))
        distantLetter = LetterPreview(2, Coordinate(37.549305, 127.077007))
    }

    @Test
    fun `내 위치와 쪽지와의 거리가 50m 보다 크면 값이 false 이다`() {
        // given
        val currentMyCoordinate = myCoordinate
        val letter = distantLetter

        // when
        letter.coordinate.isNearBy(currentMyCoordinate)

        // then
        Assertions.assertThat(letter.isNearBy).isFalse
    }

    @Test
    fun `내 위치와 쪽지와의 거리가 50m 보다 크면 값이 true 이다`() {
        // given
        val currentMyCoordinate = myCoordinate
        val letter = closeLetter

        // when
        letter.coordinate.isNearBy(currentMyCoordinate)

        // then
        Assertions.assertThat(letter.isNearBy).isTrue
    }
}
