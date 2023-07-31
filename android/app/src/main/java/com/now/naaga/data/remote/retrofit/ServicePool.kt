package com.now.naaga.data.remote.retrofit

import com.now.naaga.data.remote.retrofit.RetrofitFactory.retrofit
import com.now.naaga.data.remote.retrofit.service.Adventure2Service
import com.now.naaga.data.remote.retrofit.service.AdventureService

object ServicePool {
    val adventureService = retrofit.create(AdventureService::class.java)
    val adventureService2 = retrofit.create(Adventure2Service::class.java)
}
