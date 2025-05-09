package com.coditory.kttp.server

import com.coditory.kttp.HttpRequestHead

fun interface HttpHandlerAction {
    suspend fun handle(exchange: HttpExchange): HttpResponse
}

interface HttpHandler : HttpHandlerAction {
    fun matches(request: HttpRequestHead): Boolean

    companion object {
        fun matching(matcher: HttpRequestMatcher, handler: HttpHandlerAction): HttpHandler = HttpMatchingHandler(matcher, handler)
    }
}

internal data class HttpMatchingHandler(
    val matcher: HttpRequestMatcher,
    val handler: HttpHandlerAction,
) : HttpHandler {
    override fun matches(request: HttpRequestHead) = matcher.matches(request)
    override suspend fun handle(exchange: HttpExchange) = handler.handle(exchange)
}
