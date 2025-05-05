package com.coditory.ktserver

import com.coditory.ktserver.http.HttpRequest
import com.coditory.ktserver.http.HttpRequestMethod
import com.coditory.ktserver.http.PathPattern

interface HttpHandler {
    val pathPattern: PathPattern
    fun matches(request: HttpRequest) = pathPattern.matches(request.uri.path)
    suspend fun handle(exchange: HttpExchange)
}

class SimpleHttpHandler(
    val method: HttpRequestMethod? = null,
    override val pathPattern: PathPattern,
    val consumes: String? = null,
    val produces: String? = null,
) : HttpHandler {
    override fun matches(request: HttpRequest): Boolean {
        if (method != null && method != request.method) return false
        if (!pathPattern.matches(request.uri.path)) return false
        if (consumes == null && produces == null) return true

        return pathPattern.matches(request.uri.path)
    }

    override suspend fun handle(exchange: HttpExchange) {
        TODO("Not yet implemented")
    }
}
