package com.coditory.ktserver

import com.coditory.ktserver.http.HttpResponse
import com.coditory.ktserver.http.HttpStatus

fun interface HttpErrorHandler {
    fun handle(exchange: HttpExchange, e: Throwable): HttpResponse

    companion object {
        private val DEFAULT = object : HttpErrorHandler {
            override fun handle(exchange: HttpExchange, e: Throwable): HttpResponse {
                return HttpResponse.SentResponse(HttpStatus.InternalServerError)
            }
        }

        fun default() = DEFAULT
    }
}
