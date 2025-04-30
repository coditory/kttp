package com.coditory.kttp.server.core

import com.coditory.kttp.server.HttpChain
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpFilter
import com.coditory.kttp.server.HttpResponse

internal class HttpFilterChain(
    private val filter: HttpFilter,
    private val chain: HttpChain,
) : HttpChain {
    override suspend fun doFilter(exchange: HttpExchange): HttpResponse {
        return filter.doFilter(exchange, chain)
    }
}
