package com.now.naaga.data.remote.retrofit

import com.now.naaga.data.remote.retrofit.RetrofitFactory.retrofit
import com.now.naaga.data.remote.retrofit.service.AdventureService
import com.now.naaga.data.remote.retrofit.service.RankService
import com.now.naaga.data.remote.retrofit.service.StatisticsService

object ServicePool {
    val adventureService = retrofit.create(AdventureService::class.java)
    val rankService = retrofit.create(RankService::class.java)
    val statisticsService = retrofit.create(StatisticsService::class.java)
}
