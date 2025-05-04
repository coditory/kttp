package com.coditory.ktserver

interface HttpChain {
    suspend fun doFilter(exchange: HttpExchange)
}
