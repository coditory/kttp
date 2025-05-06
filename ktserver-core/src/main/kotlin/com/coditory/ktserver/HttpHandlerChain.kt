package com.coditory.ktserver

import com.coditory.ktserver.http.HttpResponse

open class HttpHandlerChain(
    private val action: HttpAction,
) : HttpChain {
    override suspend fun doFilter(exchange: HttpExchange): HttpResponse {
        return action.handle(exchange)
    }
}
