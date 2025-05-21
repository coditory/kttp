package com.coditory.kttp.server.filter

import com.coditory.kttp.HttpRequestMethod
import com.coditory.kttp.HttpStatus
import com.coditory.kttp.headers.HttpHeaders
import com.coditory.kttp.server.HttpChain
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpFilter
import com.coditory.kttp.server.HttpResponse
import com.coditory.kttp.server.HttpRouter

class OptionsHttpFilter(
    private val router: HttpRouter,
    private val additionalMethods: Set<HttpRequestMethod> = emptySet(),
) : HttpFilter {
    override suspend fun doFilter(exchange: HttpExchange, chain: HttpChain): HttpResponse {
        if (exchange.request.method != HttpRequestMethod.OPTIONS) {
            return chain.doFilter(exchange)
        }
        if (exchange.request.headers.contains(HttpHeaders.AccessControlRequestMethod)) {
            return chain.doFilter(exchange)
        }
        val matchers = router.getRequestMatchers(exchange.request.uri.path)
        if (matchers.isEmpty()) {
            HttpResponse.StatusResponse(HttpStatus.NotFound)
        }
        val methods = matchers.map { it.methods }
            .flatMap { it }
            .plus(additionalMethods)
            .toSet()
        return HttpResponse.StatusResponse(
            HttpStatus.NoContent,
            HttpHeaders.from(
                HttpHeaders.Allow to methods.joinToString(", "),
            ),
        )
    }
}
