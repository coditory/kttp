package com.coditory.kttp.server.filter

import com.coditory.kttp.HttpRequestMethod
import com.coditory.kttp.HttpStatus
import com.coditory.kttp.headers.HttpHeaders
import com.coditory.kttp.headers.MutableHttpHeaders
import com.coditory.kttp.server.HttpChain
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpFilter
import com.coditory.kttp.server.HttpRequest
import com.coditory.kttp.server.HttpRequestMatcher
import com.coditory.kttp.server.HttpResponse
import com.coditory.kttp.server.HttpRouter
import kotlin.time.Duration

class CorsHttpFilter(
    private val router: HttpRouter,
    private val additionalMethods: Set<HttpRequestMethod> = emptySet(),
    methods: Set<HttpRequestMethod> = emptySet(),
    headers: Set<String> = emptySet(),
    private val allowOriginAny: Boolean = false,
    private val allowOrigins: Set<String>? = null,
    private val maxAge: Duration? = null,
) : HttpFilter {
    private val allowMethods = CorsSafelist.methods.plus(methods)
    private val allowHeaders = CorsSafelist.headers.plus(headers).map { it.lowercase() }.toSet()

    override suspend fun doFilter(exchange: HttpExchange, chain: HttpChain): HttpResponse {
        if (exchange.request.method != HttpRequestMethod.OPTIONS) {
            return chain.doFilter(exchange)
        }
        if (!exchange.request.headers.contains(HttpHeaders.AccessControlRequestMethod)) {
            return chain.doFilter(exchange)
        }
        val matchers = router.getRequestMatchers(exchange.request.uri.path)
        if (matchers.isEmpty()) {
            HttpResponse.StatusResponse(HttpStatus.NotFound)
        }
        val headers = MutableHttpHeaders.empty()
        handleOrigin(exchange.request, headers)
        handleMethods(matchers, headers)
        handleHeaders(exchange.request, headers)
        handleMaxAge(headers)
        return HttpResponse.StatusResponse(HttpStatus.OK, headers)
    }

    private fun handleOrigin(request: HttpRequest, headers: MutableHttpHeaders) {
        val origin = request.headers[HttpHeaders.Origin]
        if (allowOriginAny) {
            headers.add(HttpHeaders.AccessControlAllowOrigin, "*")
        } else if (origin != null && allowOrigins?.contains(origin) ?: true) {
            headers.add(HttpHeaders.AccessControlAllowOrigin, origin)
        } else if (!allowOrigins.isNullOrEmpty()) {
            headers.add(HttpHeaders.AccessControlAllowOrigin, allowOrigins.first())
        }
    }

    private fun handleMethods(matchers: List<HttpRequestMatcher>, headers: MutableHttpHeaders) {
        val methods = matchers.map { it.methods }
            .flatMap { it }
            .plus(additionalMethods)
            .filter { allowMethods.contains(it) }
            .toSet()
        headers.add(HttpHeaders.AccessControlAllowMethods, methods.joinToString(", "))
    }

    private fun handleHeaders(request: HttpRequest, headers: MutableHttpHeaders) {
        val controlHeaders = request.headers[HttpHeaders.AccessControlAllowHeaders]?.split(" *, *")
        if (controlHeaders.isNullOrEmpty()) {
            return
        }
        val allowed = controlHeaders
            .map { it.lowercase() }
            .filter { allowHeaders.contains(it.lowercase()) }
            .toList()
        headers.add(HttpHeaders.AccessControlAllowHeaders, allowed)
    }

    private fun handleMaxAge(headers: MutableHttpHeaders) {
        if (maxAge == null) return
        headers.add(HttpHeaders.AccessControlMaxAge, maxAge.inWholeSeconds.toString())
    }

    private object CorsSafelist {
        val headers = setOf(
            HttpHeaders.Accept,
            HttpHeaders.AcceptLanguage,
            HttpHeaders.ContentLanguage,
            HttpHeaders.ContentType,
            HttpHeaders.Range,
        ).map { it.lowercase() }.toSet()
        val methods = setOf(
            HttpRequestMethod.GET,
            HttpRequestMethod.POST,
            HttpRequestMethod.HEAD,
        )
    }
}
