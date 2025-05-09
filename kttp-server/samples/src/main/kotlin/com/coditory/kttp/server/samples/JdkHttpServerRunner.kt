package com.coditory.kttp.server.samples

import com.coditory.kttp.HttpStatus
import com.coditory.kttp.server.HttpErrorHandler
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpHandler
import com.coditory.kttp.server.HttpResponse
import com.coditory.kttp.server.jdk.JdkHttpServer

object JdkHttpServerRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        val server = JdkHttpServer(
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
