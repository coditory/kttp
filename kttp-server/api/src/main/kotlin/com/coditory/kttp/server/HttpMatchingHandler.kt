package com.coditory.kttp.server

data class HttpMatchingHandler(
    val matcher: HttpRequestMatcher,
    val handler: HttpHandler,
) : HttpHandler, HttpRequestMatcher by matcher {
    override suspend fun handle(exchange: HttpExchange): HttpResponse {
        return handler.handle(exchange)
    }
}
