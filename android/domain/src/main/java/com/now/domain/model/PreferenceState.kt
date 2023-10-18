package com.now.domain.model

enum class PreferenceState {
    NONE {
        override fun select(pref: PreferenceState): PreferenceState {
            return pref
        }
    },
    LIKE {
        override fun select(pref: PreferenceState): PreferenceState {
            if (pref == LIKE) return NONE
            return pref
        }
    },
    DISLIKE {
        override fun select(pref: PreferenceState): PreferenceState {
            if (pref == DISLIKE) return NONE
            return pref
        }
    }, ;

    abstract fun select(pref: PreferenceState): PreferenceState
}
