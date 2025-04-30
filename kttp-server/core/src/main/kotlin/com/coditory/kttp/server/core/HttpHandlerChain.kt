package com.coditory.kttp.server.core

import com.coditory.kttp.server.HttpChain
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpHandler
import com.coditory.kttp.server.HttpResponse

internal open class HttpHandlerChain(
    private val action: HttpHandler,
) : HttpChain {
    override suspend fun doFilter(exchange: HttpExchange): HttpResponse {
        return action.handle(exchange)
    }
}
