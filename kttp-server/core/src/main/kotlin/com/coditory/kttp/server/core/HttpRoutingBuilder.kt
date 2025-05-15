package com.coditory.kttp.server.core

import com.coditory.kttp.server.HttpFilter
import com.coditory.kttp.server.HttpHandler
import com.coditory.kttp.server.HttpMatchingFilter
import com.coditory.kttp.server.HttpMatchingHandler
import com.coditory.kttp.server.HttpRequestMatcher
import com.coditory.kttp.server.HttpRoute

internal class HttpRoutingBuilder(
    private val parentMatcher: HttpRequestMatcher = HttpRequestMatcher.matchingAll(),
) : HttpRoute {
    private val handlers = mutableListOf<HttpMatchingHandler>()
    private val filters = mutableListOf<HttpMatchingFilter>()
    private var closed = false

    internal fun close() {
        closed = true
    }

    internal fun handlers(): List<HttpMatchingHandler> = handlers
    internal fun filters(): List<HttpMatchingFilter> = filters

    override fun routing(
        matcher: HttpRequestMatcher,
        config: HttpRoute.() -> Unit,
    ) {
        require(!closed) { "Routing already closed for modifications" }
        val childMatcher = parentMatcher.subMatcher(matcher)
        val builder = HttpRoutingBuilder(childMatcher)
        with(builder, config)
        builder.close()
        this.handlers.addAll(builder.handlers)
        this.filters.addAll(builder.filters)
    }

    override fun filter(
        matcher: HttpRequestMatcher,
        filter: HttpFilter,
    ) {
        require(!closed) { "Routing already closed for modifications" }
        val matchingFilter = HttpMatchingFilter(
            matcher = parentMatcher.subMatcher(matcher),
            filter = filter,
        )
        filters.add(matchingFilter)
    }

    override fun handler(
        matcher: HttpRequestMatcher,
        handler: HttpHandler,
    ) {
        require(!closed) { "Routing already closed for modifications" }
        val matchingHandler = HttpMatchingHandler(
            matcher = parentMatcher.subMatcher(matcher),
            handler = handler,
        )
        handlers.add(matchingHandler)
    }
}
