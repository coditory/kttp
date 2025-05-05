package com.coditory.ktserver.nio

import com.coditory.ktserver.HttpExchange
import com.coditory.ktserver.HttpSerDeserializer
import com.coditory.ktserver.HttpServer
import com.coditory.ktserver.http.HttpParams
import com.coditory.ktserver.http.HttpRequest
import com.coditory.ktserver.http.HttpRequestMethod
import com.coditory.ktserver.serialization.ScoredHttpSerDeserializer
import com.coditory.quark.uri.Ports
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.io.asSource
import kotlinx.io.buffered
import java.net.InetSocketAddress
import com.sun.net.httpserver.HttpExchange as JdkHttpExchange
import com.sun.net.httpserver.HttpServer as JdkHttpServer

class KtJdkServer(
    val port: Int = Ports.getNextAvailable(),
    val backlog: Int = 0,
    val requestScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    val serde: HttpSerDeserializer = ScoredHttpSerDeserializer.default(),
) : HttpServer {
    private val server = JdkHttpServer.create(InetSocketAddress(port), backlog).apply {
        executor = null // creates a default executor that runs on callers thread
        createContext("/") { exchange ->
            requestScope.launch {
                handleRequest(exchange)
            }
        }
    }

    private suspend fun handleRequest(srcExchange: JdkHttpExchange) {
        val request = HttpRequest(
            method = HttpRequestMethod.valueOf(srcExchange.requestMethod),
            uri = srcExchange.requestURI,
            headers = HttpParams.fromMultiMap(srcExchange.requestHeaders.toMap()),
            deserializer = serde,
            source = srcExchange.requestBody.asSource().buffered(),
        )
        val exchange = HttpExchange(request = request)
    }

    override fun start() {
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun route(path: String) {
        TODO("Not yet implemented")
    }
}
