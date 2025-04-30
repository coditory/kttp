package com.coditory.kttp.server.core

import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpHandler
import com.coditory.kttp.server.HttpRequestMatcher
import com.coditory.kttp.server.HttpResponse

internal data class HttpMatchingHandler(
    val matcher: HttpRequestMatcher,
    val handler: HttpHandler,
) : HttpHandler, HttpRequestMatcher by matcher {
    override suspend fun handle(exchange: HttpExchange): HttpResponse {
        return handler.handle(exchange)
    }
}
