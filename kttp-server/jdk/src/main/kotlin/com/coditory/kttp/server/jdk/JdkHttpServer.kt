package com.coditory.kttp.server.jdk

import com.coditory.kttp.serialization.HttpSerDeserializer
import com.coditory.kttp.serialization.SerDeserializer
import com.coditory.kttp.server.HttpErrorHandler
import com.coditory.kttp.server.HttpHandler
import com.coditory.kttp.server.HttpRoute
import com.coditory.kttp.server.HttpRouter
import com.coditory.kttp.server.HttpServer
import com.coditory.kttp.server.NotFoundHttpHandler
import com.coditory.kttp.server.core.HttpExchangeExecutor
import com.coditory.quark.uri.Ports
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress
import com.sun.net.httpserver.HttpServer as JdkHttpServer

class JdkHttpServer(
    val port: Int = Ports.getNextAvailable(8000),
    backlog: Int = 0,
    requestScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    responseWriteScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    serde: SerDeserializer = HttpSerDeserializer.default(),
    notFoundHandler: HttpHandler = NotFoundHttpHandler(),
    errorHandler: HttpErrorHandler = HttpErrorHandler.default(),
) : HttpServer {
    private val executor = HttpExchangeExecutor(
        notFoundHandler = notFoundHandler,
        errorHandler = errorHandler,
        responseSender = JdkHttpResponseSender(serde, responseWriteScope),
    )
    private val exchangeHandler = JdkHttpExchangeHandler(
        requestScope = requestScope,
        serde = serde,
        executor = executor,
    )
    private val server = JdkHttpServer.create(InetSocketAddress(port), backlog).apply {
        executor = null // creates a default executor that runs on callers thread
        createContext("/", exchangeHandler)
    }

    override fun start() {
        executor.start()
        server.start()
    }

    override fun stop() {
        runBlocking {
            executor.stopAndWait()
        }
        server.stop(0)
    }

    override fun router(): HttpRouter = executor

    override fun routing(config: HttpRoute.() -> Unit) {
        executor.routing(config = config)
    }
}
