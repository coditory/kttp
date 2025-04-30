package com.coditory.kttp.server

import com.coditory.kttp.HttpStatus

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
