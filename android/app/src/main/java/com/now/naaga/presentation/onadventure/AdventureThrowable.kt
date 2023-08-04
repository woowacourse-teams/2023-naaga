package com.now.naaga.presentation.onadventure

sealed class AdventureThrowable : Throwable() {
    class BeginAdventureFailure() : AdventureThrowable()
    class EndAdventureFailure() : AdventureThrowable() {
        override val message: String = "아직 도착하지 못했어요"
    }
    class GiveUpAdventureFailure() : AdventureThrowable()
    class HintFailure() : AdventureThrowable()
    class UnExpectedFailure() : AdventureThrowable()
}
