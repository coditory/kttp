package com.coditory.ktserver

import com.coditory.ktserver.http.HttpResponse

fun interface HttpChain {
    suspend fun doFilter(exchange: HttpExchange): HttpResponse
}
