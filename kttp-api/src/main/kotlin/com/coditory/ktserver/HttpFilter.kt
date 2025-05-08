package com.coditory.ktserver

import com.coditory.ktserver.http.HttpResponse

interface HttpFilter {
    suspend fun doFilter(exchange: HttpExchange, chain: HttpChain): HttpResponse
}
