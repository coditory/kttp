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
    val method: HttpRequestMethod,
    override val pathPattern: PathPattern,
    val consumes: String,
) : HttpHandler {
    override fun matches(request: HttpRequest): Boolean {
        return request.method == method && pathPattern.matches(request.uri.path)
    }

    override suspend fun handle(exchange: HttpExchange) {
        TODO("Not yet implemented")
    }
}
