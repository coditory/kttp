package com.coditory.ktserver

import com.sun.net.httpserver.HttpExchange

interface HttpFilter {
    suspend fun doFilter(exchange: HttpExchange, chain: HttpChain)
}
