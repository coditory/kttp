package com.coditory.kttp.server.core.base

import com.coditory.kttp.serialization.HttpSerDeserializer
import com.coditory.kttp.serialization.SerDeserializer
import com.coditory.kttp.server.HttpErrorHandler
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpHandler
import com.coditory.kttp.server.HttpRequest
import com.coditory.kttp.server.HttpResponse
import com.coditory.kttp.server.HttpRoute
import com.coditory.kttp.server.HttpRouter
import com.coditory.kttp.server.HttpServer
import com.coditory.kttp.server.core.HttpExchangeExecutor
import com.coditory.kttp.server.handler.NotFoundHttpHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer

class InMemServer(
    requestScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    serde: SerDeserializer = HttpSerDeserializer.default(),
    notFoundHandler: HttpHandler = NotFoundHttpHandler(),
    errorHandler: HttpErrorHandler = HttpErrorHandler.default(),
) : HttpServer {
    private val sender = InMemResponseSender(serde)
    private val executor = HttpExchangeExecutor(
        notFoundHandler = notFoundHandler,
        errorHandler = errorHandler,
        responseSender = sender,
    )
    private var started = false

    init {
        start()
    }

    override fun start() {
        require(!started) { "Server already started" }
        executor.start()
        started = true
    }

    override fun stop() {
        require(started) { "Server already stopped" }
        runBlocking {
            executor.stopAndWait()
        }
        started = false
    }

    override fun router(): HttpRouter = executor

    override fun routing(config: HttpRoute.() -> Unit) {
        executor.routing(config = config)
    }

    suspend fun handle(request: HttpRequest): HttpResponse {
        require(started) { "Server stopped" }
        val exchange = HttpExchange(
            request = request,
            responseBody = Buffer(),
        )
        return executor.execute(exchange)!!
    }
}
