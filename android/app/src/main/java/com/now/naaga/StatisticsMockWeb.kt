package com.now.naaga

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class StatisticsMockWeb {
    private val mockWebServer = MockWebServer()

    lateinit var url: String

    init {
        val thread = Thread {
            mockWebServer.dispatcher = dispatcher
            mockWebServer.url("/")
            url = mockWebServer.url("").toString()
        }
        thread.start()
        thread.join()
    }

    companion object {
        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: return MockResponse().setResponseCode(404)
                return when (request.method) {
                    "GET" -> {
                        when {
                            path.startsWith("/statistics/my") -> {
                                MockResponse()
                                    .setHeader("Authorization", "Basic MTExQHdvb3dhLmNvbToxMTEx")
                                    .setResponseCode(200)
                                    .setBody(getStatistics())
                            }
                            else -> MockResponse().setResponseCode(401)
                        }
                    }
                    else -> MockResponse().setResponseCode(401)
                }
            }
        }

        private fun getStatistics(): String {
            return """
                {
                	"gameCount": 147,
                	"successGameCount": 123,
                	"failGameCount": 24,
                	"totalDistance": 101289,
                	"totalPlayTime": "124:23:59.123",
                	"totalUsedHintCount": 123
                }
            """.trimIndent()
        }
    }
}
