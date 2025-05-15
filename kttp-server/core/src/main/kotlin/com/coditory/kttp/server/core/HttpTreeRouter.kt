package com.coditory.kttp.server.core

import com.coditory.klog.Klog
import com.coditory.kttp.HttpRequestHead
import com.coditory.kttp.HttpStatus
import com.coditory.kttp.server.HttpChain
import com.coditory.kttp.server.HttpErrorHandler
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpFilter
import com.coditory.kttp.server.HttpHandler
import com.coditory.kttp.server.HttpMatchingFilter
import com.coditory.kttp.server.HttpMatchingHandler
import com.coditory.kttp.server.HttpRequestMatcher
import com.coditory.kttp.server.HttpResponse
import com.coditory.kttp.server.HttpResponseSender
import com.coditory.kttp.server.HttpRoute
import com.coditory.kttp.server.HttpRouter

class HttpTreeRouter(
    private val responseSender: HttpResponseSender = HttpResponseSender.default(),
    private val notFoundHandler: HttpHandler = NotFoundHttpHandler(),
    private val errorHandler: HttpErrorHandler = HttpErrorHandler.default(),
) : HttpRouter {
    private val log = Klog.logger(HttpRouter::class)
    private val root = HttpTreeRouterNode()

    suspend fun handle(exchange: HttpExchange) {
        val response = try {
            val chain = chain(exchange.request.toHead())
            chain.doFilter(exchange)
        } catch (e: Throwable) {
            log.error(e) { "Failed for exchange: ${exchange.request.method} ${exchange.request.uri}" }
            errorHandler.handle(exchange, e)
        }
        if (response is HttpResponse.SentResponse) return
        try {
            responseSender.sendResponse(exchange, response)
        } catch (e: Throwable) {
            log.error(e) { "Failed sending response for: ${exchange.request.method} ${exchange.request.uri}" }
            responseSender.sendResponse(exchange, HttpResponse.StatusResponse(HttpStatus.InternalServerError))
        }
    }

    override fun chain(request: HttpRequestHead): HttpChain {
        return root.getChain(request, notFoundHandler)
    }

    override fun matchingHandlers(path: String): List<HttpRequestMatcher> {
        return root.matchingHandlers(path)
    }

    private fun addHandlers(handlers: List<HttpMatchingHandler>) {
        handlers.forEach { addHandler(it) }
    }

    private fun addHandler(handler: HttpMatchingHandler) {
        root.addHandler(handler)
    }

    override fun removeHandler(handler: HttpHandler) {
        root.removeHandler(handler)
    }

    private fun addFilters(filters: List<HttpMatchingFilter>) {
        filters.forEach { addFilter(it) }
    }

    private fun addFilter(filter: HttpMatchingFilter) {
        root.addFilter(filter)
    }

    override fun removeFilter(filter: HttpFilter) {
        root.removeFilter(filter)
    }

    override fun routing(
        matcher: HttpRequestMatcher,
        config: HttpRoute.() -> Unit,
    ) {
        val builder = HttpRoutingBuilder()
        builder.routing(matcher, config)
        addFilters(builder.filters())
        addHandlers(builder.handlers())
    }

    override fun filter(matcher: HttpRequestMatcher, filter: HttpFilter) {
        addFilter(HttpMatchingFilter(matcher, filter))
    }

    override fun handler(matcher: HttpRequestMatcher, handler: HttpHandler) {
        addHandler(HttpMatchingHandler(matcher, handler))
    }
}
