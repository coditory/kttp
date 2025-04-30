package com.coditory.kttp.server.jdk

import com.coditory.kttp.HttpRequestMethod
import com.coditory.kttp.headers.HttpHeaders
import com.coditory.kttp.serialization.SerDeserializer
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpRequest
import com.coditory.kttp.server.core.HttpExchangeExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import com.sun.net.httpserver.HttpExchange as JdkHttpExchange
import com.sun.net.httpserver.HttpHandler as JdkHttpHandler

@OptIn(DelicateCoroutinesApi::class)
internal class JdkHttpExchangeHandler(
    private val requestScope: CoroutineScope,
    private val serde: SerDeserializer,
    private val executor: HttpExchangeExecutor,
) : JdkHttpHandler {
    override fun handle(jdkExchange: JdkHttpExchange) {
        requestScope.launch {
            handleAndCatch(jdkExchange)
        }
    }

    private suspend fun handleAndCatch(jdkExchange: JdkHttpExchange) {
        try {
            val exchange = buildExchange(jdkExchange)
            executor.execute(exchange)
        } catch (_: Throwable) {
            jdkExchange.sendResponseHeaders(500, 0)
        }
    }

    private fun buildExchange(jdkExchange: JdkHttpExchange): HttpExchange {
        val request = HttpRequest(
            method = HttpRequestMethod.from(jdkExchange.requestMethod),
            uri = jdkExchange.requestURI,
            headers = HttpHeaders.fromMultiMap(jdkExchange.requestHeaders.toMap()),
            deserializer = serde,
            source = jdkExchange.requestBody.asSource().buffered(),
        )
        val exchange = HttpExchange(
            request = request,
            responseBody = jdkExchange.responseBody.asSink().buffered(),
        )
        exchange.attributes["jdkExchange"] = jdkExchange
        return exchange
    }
}
