package com.coditory.kttp.server

fun interface HttpFilter {
    suspend fun doFilter(exchange: HttpExchange, chain: HttpChain): HttpResponse
}
