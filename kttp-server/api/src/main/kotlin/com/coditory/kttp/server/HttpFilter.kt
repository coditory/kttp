package com.coditory.kttp.server

interface HttpFilter {
    suspend fun doFilter(exchange: HttpExchange, chain: HttpChain): HttpResponse
}
