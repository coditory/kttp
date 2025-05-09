package com.coditory.kttp.server

fun interface HttpChain {
    suspend fun doFilter(exchange: HttpExchange): HttpResponse
}
