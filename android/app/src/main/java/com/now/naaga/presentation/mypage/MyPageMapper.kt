package com.now.naaga.presentation.mypage

import android.content.Context
import com.now.domain.model.Place
import com.now.domain.model.Statistics
import com.now.naaga.R
import com.now.naaga.presentation.mypage.place.MyPagePlaceUiModel
import com.now.naaga.presentation.mypage.place.MyPageStatisticsUiModel

fun Statistics.toUiModel(context: Context): List<MyPageStatisticsUiModel> {
    return listOf(
        MyPageStatisticsUiModel(
            adventureCount,
            context.getString(R.string.mypage_count),
            context.getString(R.string.mypage_total_adventure),
        ),
        MyPageStatisticsUiModel(
            successCount,
            context.getString(R.string.mypage_count),
            context.getString(R.string.mypage_adventure_success),
        ),
        MyPageStatisticsUiModel(
            failureCount,
            context.getString(R.string.mypage_count),
            context.getString(R.string.mypage_adventure_failure),
        ),
        MyPageStatisticsUiModel(
            totalPlayTime,
            context.getString(R.string.mypage_minute),
            context.getString(R.string.mypage_total_play_time),
        ),
        MyPageStatisticsUiModel(
            totalDistance,
            context.getString(R.string.mypage_meter),
            context.getString(R.string.mypage_total_adventure_distance),
        ),
        MyPageStatisticsUiModel(
            totalHintUses,
            context.getString(R.string.mypage_number),
            context.getString(R.string.mypage_total_hint_uses),
        ),
    )
}

fun Place.toUiModel(): MyPagePlaceUiModel {
    return MyPagePlaceUiModel(
        image,
        name,
    )
}
