package com.coditory.kttp.server.core

import com.coditory.kttp.server.HttpChain
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpHandlerAction
import com.coditory.kttp.server.HttpResponse

open class HttpHandlerChain(
    private val action: HttpHandlerAction,
) : HttpChain {
    override suspend fun doFilter(exchange: HttpExchange): HttpResponse {
        return action.handle(exchange)
    }
}
