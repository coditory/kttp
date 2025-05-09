package com.coditory.kttp.server.core

import com.coditory.kttp.server.HttpFilter
import com.coditory.kttp.server.HttpHandler
import com.coditory.kttp.server.HttpRequest
import com.coditory.kttp.server.HttpRoute
import com.coditory.kttp.server.PathPattern

class HttpRouteNode : HttpRoute {
    private val children = mutableMapOf<String, HttpRouteNode>()
    private val genericPathChunks = mutableSetOf<String>()
    private val handlers = mutableListOf<HttpHandler>()
    private val filters = mutableListOf<HttpFilter>()

    fun getMatchingHandler(request: HttpRequest): HttpHandler? {
        return handlers.find { it.matches(request) }
    }

    fun getFilters(): List<HttpFilter> {
        return filters
    }

    fun aggregate(path: String): List<HttpRouteNode> {
        val chunks = path.split("/").filter { it.isNotBlank() }
        val routes = mutableListOf<HttpRouteNode>()
        aggregate(chunks, routes)
        return routes
    }

    private fun aggregate(chunks: List<String>, result: MutableList<HttpRouteNode>) {
        result.add(this)
        if (chunks.isEmpty()) return
        val key = chunks.first()
        val child = children[key]
        child?.aggregate(chunks.drop(1), result)
        genericPathChunks
            .mapNotNull { children[it] }
            .forEach { it.aggregate(chunks.drop(1), result) }
    }

    fun child(path: String): HttpRouteNode {
        val chunks = path.split("/").filter { it.isNotBlank() }
        if (chunks.isEmpty()) return this
        return child(chunks)
    }

    private fun child(chunks: List<String>): HttpRouteNode {
        if (chunks.isEmpty()) return this
        val key = chunks.first()
        val child = children[key] ?: HttpRouteNode()
        children[key] = child
        if (PathPattern.isGenericPathChunk(key)) {
            genericPathChunks.add(key)
        }
        return child.child(chunks.drop(1))
    }

    fun addHandler(handler: HttpHandler) {
        handlers.add(handler)
    }

    fun addFilter(filter: HttpFilter) {
        filters.add(filter)
    }

    override fun routing(path: String, config: HttpRoute.() -> Unit) {
        val node = child(path)
        with(node, config)
    }

    override fun filter(filter: HttpFilter) {
        filters.add(filter)
    }

    override fun handler(handler: HttpHandler) {
        handlers.add(handler)
    }
}
