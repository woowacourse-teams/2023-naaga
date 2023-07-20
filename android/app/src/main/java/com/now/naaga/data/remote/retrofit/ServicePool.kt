package com.now.naaga.data.remote.retrofit

import com.now.naaga.data.remote.retrofit.RetrofitFactory.retrofit
import com.now.naaga.data.remote.retrofit.service.AdventureService

object ServicePool {
    val adventureService = retrofit.create(AdventureService::class.java)
}
