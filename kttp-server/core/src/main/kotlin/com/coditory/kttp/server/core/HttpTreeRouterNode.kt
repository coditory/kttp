package com.coditory.kttp.server.core

import com.coditory.kttp.HttpRequestHead
import com.coditory.kttp.server.HttpChain
import com.coditory.kttp.server.HttpFilter
import com.coditory.kttp.server.HttpHandler
import com.coditory.kttp.server.HttpMatchingFilter
import com.coditory.kttp.server.HttpMatchingHandler

internal class HttpTreeRouterNode {
    private val children = mutableMapOf<String, HttpTreeRouterNode>()
    private val handlers = mutableListOf<HttpMatchingHandler>()
    private val filters = mutableListOf<HttpMatchingFilter>()

    fun matchingHandlers(path: String): List<HttpMatchingHandler> {
        val pathChunks = path.split("/").filter { it.isNotEmpty() }
        val result = mutableListOf<HttpMatchingHandler>()
        matchingHandlers(pathChunks, path, result)
        return result
    }

    private fun matchingHandlers(chunks: List<String>, path: String, result: MutableList<HttpMatchingHandler>) {
        result.addAll(handlers.filter { it.matchesPath(path) })
        if (chunks.isEmpty()) return
        val key = chunks.first()
        val rest = chunks.drop(1)
        children[key]?.matchingHandlers(rest, path, result)
        children["*"]?.matchingHandlers(rest, path, result)
        children["**"]?.matchingHandlers(rest, path, result)
    }

    fun addHandler(handler: HttpMatchingHandler) {
        val pathPattern = handler.pathPattern
        val pathChunks = pathPattern?.pattern
            ?.split("/")?.filter { it.isNotEmpty() }
            ?: emptyList()
        addHandler(pathChunks, handler)
    }

    private fun addHandler(pathChunks: List<String>, handler: HttpMatchingHandler) {
        if (pathChunks.isEmpty()) {
            handlers.add(handler)
            return
        }
        val childKey = pathChunks.first()
        val child = children.getOrPut(childKey) { HttpTreeRouterNode() }
        child.addHandler(pathChunks.drop(1), handler)
    }

    fun removeHandler(handler: HttpHandler) {
        this.handlers.filter { it != handler && it.handler != handler }
        children.forEach { key, child -> child.removeHandler(handler) }
    }

    fun addFilter(filter: HttpMatchingFilter) {
        val pathPattern = filter.pathPattern
        val pathChunks = pathPattern?.pattern?.split("/")?.filter { it.isNotEmpty() }
        addFilter(pathChunks ?: emptyList(), filter)
    }

    private fun addFilter(pathChunks: List<String>, filter: HttpMatchingFilter) {
        if (pathChunks.isEmpty()) {
            filters.add(filter)
            return
        }
        val childKey = pathChunks.first()
        val child = children.getOrPut(childKey) { HttpTreeRouterNode() }
        child.addFilter(pathChunks.drop(1), filter)
    }

    fun removeFilter(filter: HttpFilter) {
        this.filters.filter { it != filter && it.filter != filter }
        children.forEach { key, child -> child.removeFilter(filter) }
    }

    fun getChain(request: HttpRequestHead, defaultHandler: HttpHandler): HttpChain {
        val pathChunks = request.uri.path.split("/").filter { it.isNotEmpty() }
        val handler = findFirstMatchingHandler(pathChunks, request) ?: defaultHandler
        val filters = findAllMatchingFilters(pathChunks, request)
        val handlerChain = HttpHandlerChain(handler)
        return buildFilterChain(filters, handlerChain)
    }

    private fun buildFilterChain(filters: List<HttpMatchingFilter>, terminating: HttpChain): HttpChain {
        var next = terminating
        for (i in filters.size - 1 downTo 0) {
            val filter = filters[i]
            val chain = HttpFilterChain(filter, next)
            next = chain
        }
        return next
    }

    private fun findFirstMatchingHandler(pathChunks: List<String>, request: HttpRequestHead): HttpMatchingHandler? {
        val handler = handlers.find { it.matches(request) }
        if (handler != null) return handler
        for (childKey in listOf(pathChunks.firstOrNull(), "*", "**")) {
            if (childKey == null) continue
            val child = children[childKey] ?: continue
            val childHandler = child.findFirstMatchingHandler(pathChunks.drop(1), request)
            if (childHandler != null) return childHandler
        }
        return null
    }

    private fun findAllMatchingFilters(pathChunks: List<String>, request: HttpRequestHead): List<HttpMatchingFilter> {
        val result = mutableListOf<HttpMatchingFilter>()
        result.addAll(filters.filter { it.matches(request) })
        for (childKey in listOf(pathChunks.firstOrNull(), "*", "**")) {
            if (childKey == null) continue
            val child = children[childKey] ?: continue
            val childFilters = child.findAllMatchingFilters(pathChunks.drop(1), request)
            result.addAll(childFilters)
        }
        return result
    }
}
