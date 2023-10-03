package com.now.naaga.presentation.mypage

import com.now.domain.model.Place
import com.now.naaga.presentation.uimodel.model.MyPagePlaceUiModel

fun Place.toUiModel(): MyPagePlaceUiModel {
    return MyPagePlaceUiModel(
        image,
        name,
    )
}
