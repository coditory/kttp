package com.coditory.kttp.server

data class HttpMatchingFilter(
    val matcher: HttpRequestMatcher,
    val filter: HttpFilter,
) : HttpFilter, HttpRequestMatcher by matcher {
    override suspend fun doFilter(exchange: HttpExchange, chain: HttpChain): HttpResponse {
        return filter.doFilter(exchange, chain)
    }
}
