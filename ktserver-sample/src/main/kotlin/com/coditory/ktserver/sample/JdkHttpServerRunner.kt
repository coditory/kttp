package com.coditory.klog.sample

import com.coditory.ktserver.HttpErrorHandler
import com.coditory.ktserver.HttpExchange
import com.coditory.ktserver.HttpHandler
import com.coditory.ktserver.http.HttpResponse
import com.coditory.ktserver.http.HttpStatus
import com.coditory.ktserver.nio.KtJdkServer

object JdkHttpServerRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        val server = KtJdkServer(
            notFoundAction = HttpHandler {
                HttpResponse.TextResponse("Not found", HttpStatus.NotFound)
            },
            errorHandler = HttpErrorHandler { ex: HttpExchange, e: Throwable ->
                HttpResponse.TextResponse("Server Error: ${e.message}", HttpStatus.InternalServerError)
            },
        )
        server.routing {
            get("/") {
                HttpResponse.TextResponse("Path: /")
            }
            get("/hello") {
                HttpResponse.TextResponse("Path: /hello")
            }
            get("/hello/hello") {
                HttpResponse.TextResponse("Path: /hello/hello")
            }
            routing("/other") {
                get("/") {
                    HttpResponse.TextResponse("Path: /other")
                }
                get("/hello") {
                    HttpResponse.TextResponse("Path: /other/hello")
                }
            }
            routing("/*") {
                get("/") {
                    HttpResponse.TextResponse("Path: /other")
                }
                get("/hello") {
                    HttpResponse.TextResponse("Path: /other/hello")
                }
            }
        }
        server.start()
        println("${server::class.simpleName} listening on http://localhost:${server.port}")
        Thread.sleep(100_000)
    }
}
