package com.coditory.kttp.server.core

import com.coditory.kttp.HttpRequestHead
import com.coditory.kttp.server.HttpChain
import com.coditory.kttp.server.HttpFilter
import com.coditory.kttp.server.HttpHandler
import com.coditory.kttp.server.HttpRequestMatcher
import com.coditory.kttp.server.HttpRoute
import com.coditory.kttp.server.HttpRouter
import com.coditory.kttp.server.NotFoundHttpHandler

internal class HttpTreeRouter(
    private val notFoundHandler: HttpHandler = NotFoundHttpHandler(),
) : HttpRouter {
    private val root = HttpTreeRouterNode()

    override fun chain(request: HttpRequestHead): HttpChain {
        return root.getChain(request, notFoundHandler)
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
