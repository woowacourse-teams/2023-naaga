package com.now.domain.model

data class Preference(
    val state: PreferenceState = PreferenceState.NONE,
    val preState: PreferenceState = PreferenceState.NONE,
    val likeCount: PreferenceCount = PreferenceCount(0),
    val preLikeCount: PreferenceCount = PreferenceCount(0),
) {
    fun select(selectedState: PreferenceState): Preference {
        val newState = state.select(selectedState)
        return Preference(
            state = newState,
            preState = state,
            likeCount = updateCount(newState),
            preLikeCount = likeCount,
        )
    }

    fun revert(): Preference {
        return Preference(
            state = preState,
            likeCount = preLikeCount,
        )
    }

    private fun updateCount(newState: PreferenceState): PreferenceCount {
        return when (newState) {
            PreferenceState.NONE -> if (state == PreferenceState.LIKE) likeCount.minus() else likeCount
            PreferenceState.LIKE -> likeCount.plus()
            PreferenceState.DISLIKE -> if (state == PreferenceState.LIKE) likeCount.minus() else likeCount
        }
    }
}
