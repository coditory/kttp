package com.coditory.kttp.server.filter

import com.coditory.kttp.HttpStatus
import com.coditory.kttp.server.HttpChain
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpFilter
import com.coditory.kttp.server.HttpResponse
import com.coditory.kttp.server.HttpRouter

class NotAcceptableHttpFilter(
    private val router: HttpRouter,
) : HttpFilter {
    override suspend fun doFilter(
        exchange: HttpExchange,
        chain: HttpChain,
    ): HttpResponse {
        if (router.hasHandler(exchange.request.toHead())) {
            return chain.doFilter(exchange)
        }
        val matchers = router.getRequestMatchers(exchange.request.uri.path)
        if (matchers.isEmpty()) {
            return chain.doFilter(exchange)
        }
        val methods = matchers.map { it.methods }
            .flatMap { it }
            .toSet()
        if (!methods.contains(exchange.request.method)) {
            return HttpResponse.StatusResponse(HttpStatus.MethodNotAllowed)
        }
        val accept = exchange.request.headers.accept()
        if (accept == null) {
            return chain.doFilter(exchange)
        }
        if (matchers.any { it.matchesAccept(accept) }) {
            return chain.doFilter(exchange)
        }
        return HttpResponse.StatusResponse(HttpStatus.NotAcceptable)
    }
}
