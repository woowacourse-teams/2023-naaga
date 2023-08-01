package com.now.naaga.presentation.onadventure

sealed class AdventureThrowable : Throwable() {
    class BeginAdventureFailure() : AdventureThrowable()
    class EndAdventureFailure() : AdventureThrowable()
    class GiveUpAdventureFailure() : AdventureThrowable()
    class HintFailure() : AdventureThrowable()
    class UnExpectedFailure() : AdventureThrowable()
}
