package com.coditory.kttp.server.jdk

import com.coditory.kttp.serialization.HttpSerDeserializer
import com.coditory.kttp.serialization.SerDeserializer
import com.coditory.kttp.server.HttpErrorHandler
import com.coditory.kttp.server.HttpHandler
import com.coditory.kttp.server.HttpRoute
import com.coditory.kttp.server.HttpRouter
import com.coditory.kttp.server.HttpServer
import com.coditory.kttp.server.core.HttpTreeRouter
import com.coditory.kttp.server.core.NotFoundHttpHandler
import com.coditory.quark.uri.Ports
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.net.InetSocketAddress
import com.sun.net.httpserver.HttpServer as JdkHttpServer

class JdkHttpServer(
    val port: Int = Ports.getNextAvailable(),
    backlog: Int = 0,
    requestScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    responseWriteScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    serde: SerDeserializer = HttpSerDeserializer.default(),
    notFoundHandler: HttpHandler = NotFoundHttpHandler(),
    errorHandler: HttpErrorHandler = HttpErrorHandler.default(),
) : HttpServer {
    private val router = HttpTreeRouter(
        notFoundHandler = notFoundHandler,
        errorHandler = errorHandler,
        responseSender = JdkHttpResponseSender(serde, responseWriteScope),
    )
    private val exchangeHandler = JdkHttpExchangeHandler(
        requestScope = requestScope,
        serde = serde,
        router = router,
    )
    private val server = JdkHttpServer.create(InetSocketAddress(port), backlog).apply {
        executor = null // creates a default executor that runs on callers thread
        createContext("/", exchangeHandler)
    }

    override fun start() {
        server.start()
    }

    override fun stop() {
        server.stop(5)
    }

    override fun router(): HttpRouter = router

    override fun routing(config: HttpRoute.() -> Unit) {
        router.routing(config = config)
    }
}
