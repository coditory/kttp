package com.coditory.kttp.server.core

import com.coditory.kttp.server.HttpChain
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpFilter
import com.coditory.kttp.server.HttpRequestMatcher
import com.coditory.kttp.server.HttpResponse

internal data class HttpMatchingFilter(
    val matcher: HttpRequestMatcher,
    val filter: HttpFilter,
) : HttpFilter, HttpRequestMatcher by matcher {
    override suspend fun doFilter(exchange: HttpExchange, chain: HttpChain): HttpResponse {
        return filter.doFilter(exchange, chain)
    }

    companion object {
        fun matchingAll(filter: HttpFilter): HttpMatchingFilter {
            return HttpMatchingFilter(HttpRequestMatcher.matchingAll(), filter)
        }
    }
}
