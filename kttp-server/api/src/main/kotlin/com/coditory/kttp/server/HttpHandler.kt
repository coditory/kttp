package com.coditory.kttp.server

import com.coditory.kttp.HttpRequestHead
import com.coditory.kttp.HttpRequestMethod
import com.coditory.kttp.headers.ContentType

fun interface HttpHandlerAction {
    suspend fun handle(exchange: HttpExchange): HttpResponse
}

data class HttpHandler(
    val method: HttpRequestMethod? = null,
    val pathPattern: PathPattern? = null,
    val consumes: ContentType? = null,
    val produces: ContentType? = null,
    val action: HttpHandlerAction,
) : HttpHandlerAction {
    // TODO: Add language and others
    fun matches(request: HttpRequestHead): Boolean = matchesPath(request) &&
        matchesMethod(request) &&
        matchesAccept(request) &&
        matchesContentType(request)

    fun matchesPath(request: HttpRequestHead): Boolean {
        return pathPattern == null || pathPattern.matches(request.uri.path)
    }

    fun matchesMethod(request: HttpRequestHead): Boolean {
        return method == null || method == request.method
    }

    fun matchesAccept(request: HttpRequestHead): Boolean {
        return produces == null || request.headers.accept()?.let { produces.matches(it) } == true
    }

    fun matchesContentType(request: HttpRequestHead): Boolean {
        return consumes == null || request.headers.contentType()?.let { consumes.matches(it) } == true
    }

    override suspend fun handle(exchange: HttpExchange): HttpResponse {
        return action.handle(exchange)
    }
}
