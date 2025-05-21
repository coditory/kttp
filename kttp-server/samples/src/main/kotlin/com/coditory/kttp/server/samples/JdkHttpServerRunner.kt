package com.coditory.kttp.server.samples

import com.coditory.kttp.HttpStatus
import com.coditory.kttp.headers.MediaType
import com.coditory.kttp.server.HttpErrorHandler
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpHandler
import com.coditory.kttp.server.HttpResponse
import com.coditory.kttp.server.filter.CorsHttpFilter
import com.coditory.kttp.server.jdk.JdkHttpServer
import kotlinx.coroutines.runBlocking

object JdkHttpServerRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        val server = JdkHttpServer(
            notFoundHandler = HttpHandler {
                HttpResponse.TextResponse("Not found", HttpStatus.NotFound)
            },
            errorHandler = HttpErrorHandler { ex: HttpExchange, e: Throwable ->
                HttpResponse.TextResponse("Server Error: ${e.message}", HttpStatus.InternalServerError)
            },
        )
        server.routing {
            filter(CorsHttpFilter(server.router()))
            get("/") {
                HttpResponse.TextResponse("Path: /")
            }
            get("/hello") {
                HttpResponse.TextResponse("Path: /hello")
            }
            get(path = "/hello/hello", produces = MediaType.Text.Plain) {
                HttpResponse.TextResponse("Path: /hello/hello (text)")
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
                    HttpResponse.TextResponse("Path: /*")
                }
                get("/hello") {
                    HttpResponse.TextResponse("Path: /*/hello")
                }
            }
        }
        runBlocking {
            server.startAndWait {
                println("${server::class.simpleName} listening on http://localhost:${server.port}")
            }
        }
        server.stop()
        println("Server stopped")
    }
}
