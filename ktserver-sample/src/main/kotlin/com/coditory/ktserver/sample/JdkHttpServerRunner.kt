package com.coditory.klog.sample

import com.coditory.ktserver.http.HttpResponse
import com.coditory.ktserver.nio.KtJdkServer

object JdkHttpServerRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        val server = KtJdkServer()
        server.routing {
            get("/") {
                HttpResponse.TextResponse("Root path")
            }
            get("/hello") {
                HttpResponse.TextResponse("Hello path")
            }
            get("/hello/hello") {
                HttpResponse.TextResponse("Hello sub path")
            }
        }
        server.start()
        println("${server::class.simpleName} listening on http://localhost:${server.port}")
        Thread.sleep(100_000)
    }
}
