package com.coditory.kttp.server.filter

import com.coditory.kttp.HttpRequestMethod
import com.coditory.kttp.server.HttpChain
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpFilter
import com.coditory.kttp.server.HttpResponse
import com.coditory.kttp.server.HttpRouter

class HeadHttpFilter(
    private val router: HttpRouter,
) : HttpFilter {
    override suspend fun doFilter(exchange: HttpExchange, chain: HttpChain): HttpResponse {
        if (exchange.request.method != HttpRequestMethod.HEAD) {
            return chain.doFilter(exchange)
        }
        if (router.hasHandler(exchange.request.toHead())) {
            return chain.doFilter(exchange)
        }
        val getRequest = exchange.request.copy(
            method = HttpRequestMethod.GET,
        )
        val exchangeWithGet = exchange.copy(
            request = getRequest,
        )
        val getChain = router.chain(getRequest.toHead())
        val getResponse = getChain.doFilter(exchangeWithGet)
        if (getResponse is HttpResponse.SentResponse) {
            return getResponse
        }
        return HttpResponse.StatusResponse(getResponse.status, getResponse.headers)
    }
}
