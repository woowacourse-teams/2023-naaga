package com.now.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DestinationTest {
    @Test
    fun `거리가 50m 보다 크면 false 를 반환한다`() {
        val currentCoordinate = lutherHall
        val destCoordinate = woowaBrotherSmallHouse

        val destination = Destination(1, destCoordinate, "")
        val isArrived = destination.isArrived(currentCoordinate)

        assertThat(isArrived).isFalse
    }

    @Test
    fun `거리가 50m 보다 작으면 true 를 반환한다`() {
        val currentCoordinate = lutherHall
        val destCoordinate = subway

        val destination = Destination(1, destCoordinate, "")
        val isArrived = destination.isArrived(currentCoordinate)

        assertThat(isArrived).isTrue
    }

    companion object {
        val lutherHall = Coordinate(37.515346, 127.102900)
        val woowaBrotherSmallHouse = Coordinate(37.514907, 127.103198) // 루터회관과 51~54m
        val subway = Coordinate(37.515178, 127.102597) // 루터회관과 34~38m
    }
}
