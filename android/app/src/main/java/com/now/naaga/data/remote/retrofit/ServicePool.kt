package com.now.naaga.data.remote.retrofit

import com.now.naaga.data.remote.retrofit.RetrofitFactory.retrofit
import com.now.naaga.data.remote.retrofit.service.AdventureService
import com.now.naaga.data.remote.retrofit.service.StatisticsService
import retrofit2.create

object ServicePool {
    val adventureService = retrofit.create(AdventureService::class.java)
    val statisticsService = retrofit.create(StatisticsService::class.java)
}
