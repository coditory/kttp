package com.coditory.kttp.server

import com.coditory.kttp.HttpRequestHead

fun interface HttpHandler {
    suspend fun handle(exchange: HttpExchange): HttpResponse
}

interface HttpMatchingHandler : HttpHandler {
    fun matches(request: HttpRequestHead): Boolean

    companion object {
        fun matching(matcher: HttpRequestMatcher, handler: HttpHandler): HttpMatchingHandler = HttpCompositeMatchingHandler(matcher, handler)
    }
}

internal data class HttpCompositeMatchingHandler(
    val matcher: HttpRequestMatcher,
    val handler: HttpHandler,
) : HttpMatchingHandler {
    override fun matches(request: HttpRequestHead) = matcher.matches(request)
    override suspend fun handle(exchange: HttpExchange) = handler.handle(exchange)
}
