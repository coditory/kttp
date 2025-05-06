package com.coditory.ktserver

import com.coditory.ktserver.http.ContentType
import com.coditory.ktserver.http.HttpRequest
import com.coditory.ktserver.http.HttpRequestMethod
import com.coditory.ktserver.http.HttpResponse
import com.coditory.ktserver.http.PathPattern

interface HttpHandler : HttpAction {
    val pathPattern: PathPattern
    fun matches(request: HttpRequest) = pathPattern.matches(request.uri.path)
}

internal class SimpleHttpHandler(
    val method: HttpRequestMethod? = null,
    override val pathPattern: PathPattern,
    private val consumes: ContentType? = null,
    private val produces: ContentType? = null,
    private val action: HttpAction,
) : HttpHandler {
    override fun matches(request: HttpRequest): Boolean {
        if (method != null && method != request.method) return false
        if (!pathPattern.matches(request.uri.path)) return false
        if (consumes == null && produces == null) return true
        // TODO: Negotiate content with Content-Type and Accept
        return true
    }

    override suspend fun handle(exchange: HttpExchange): HttpResponse {
        return action.handle(exchange)
    }
}
