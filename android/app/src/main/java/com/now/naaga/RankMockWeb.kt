package com.now.naaga

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class RankMockWeb {
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
                val path = request.path ?: return MockResponse().setResponseCode(401)
                return when (request.method) {
                    "GET" -> {
                        when {
                            path.startsWith("/ranks/my") -> {
                                MockResponse()
                                    .setHeader("Authorization", "Basic MTExQHdvb3dhLmNvbToxMTEx")
                                    .setResponseCode(200)
                                    .setBody(getMyRank())
                            }

                            path.startsWith("/rank") -> {
                                val parameters = extractQueryParameters(path)
                                val sortBy = parameters["sort-by"]
                                val order = parameters["order"]
                                if (sortBy == null || order == null) {
                                    return MockResponse().setResponseCode(401)
                                }
                                MockResponse()
                                    .setHeader("Authorization", "Basic MTExQHdvb3dhLmNvbToxMTEx")
                                    .setResponseCode(200)
                                    .setBody(getAllRank())
                            }
                            else -> MockResponse().setResponseCode(401)
                        }
                    }
                    else -> MockResponse().setResponseCode(401)
                }
            }

            private fun extractQueryParameters(queryString: String): Map<String, String> {
                val parameters = mutableMapOf<String, String>()
                val query = queryString.substringAfter("?")
                val pairs = query.split("&")
                for (pair in pairs) {
                    val (key, value) = pair.split("=")
                    parameters[key] = value
                }
                return parameters
            }

            private fun getMyRank(): String {
                return """
                    {
                        "player": {
                    	    "id": 1,
                    	    "nickname": "뽀또",
                    	    "totalScore": 1324235
                    	},
                    	"percentage": 10,
                    	"rank": 1
                    }
                """.trimIndent()
            }

            private fun getAllRank(): String {
                return """
                    [
                    	{
                    		"player": {
                    			"id": 1,
                    			"nickname": "뽀또",
                    			"totalScore": 1324235
                    		},
                    		"percentage": 10,
                    		"rank": 1
                    	},
                        {
                    		"player": {
                    			"id": 2,
                    			"nickname": "크롱",
                    			"totalScore": 1322235
                    		},
                    		"percentage": 20,
                    		"rank": 2
                    	},
                        {
                            "player": {
                    			"id": 1,
                    			"nickname": "빅스",
                    			"totalScore": 1124235
                    		},
                    		"percentage": 30,
                    		"rank": 3
                        },
                    ]
                """.trimIndent()
            }
        }
    }
}
