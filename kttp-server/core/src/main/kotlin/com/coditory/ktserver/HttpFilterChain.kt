package com.coditory.ktserver

import com.coditory.ktserver.http.HttpResponse

class HttpFilterChain(
    private val filter: HttpFilter,
    private val chain: HttpChain,
) : HttpChain {
    override suspend fun doFilter(exchange: HttpExchange): HttpResponse {
        return filter.doFilter(exchange, chain)
    }
}
