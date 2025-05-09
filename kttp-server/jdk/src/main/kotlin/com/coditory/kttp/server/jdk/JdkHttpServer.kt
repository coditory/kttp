package com.coditory.kttp.server.jdk

import com.coditory.kttp.serialization.HttpSerDeserializer
import com.coditory.kttp.serialization.SerDeserializer
import com.coditory.kttp.server.HttpErrorHandler
import com.coditory.kttp.server.HttpHandlerAction
import com.coditory.kttp.server.HttpRoute
import com.coditory.kttp.server.HttpServer
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
    responseSendingScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    serde: SerDeserializer = HttpSerDeserializer.default(),
    notFoundAction: HttpHandlerAction = NotFoundHttpHandler(),
    errorHandler: HttpErrorHandler = HttpErrorHandler.default(),
) : HttpServer {
    private val router = JdkHttpRouter(
        requestScope = requestScope,
        responseSendingScope = responseSendingScope,
        notFoundAction = notFoundAction,
        errorHandler = errorHandler,
        serde = serde,
    )
    private val server = JdkHttpServer.create(InetSocketAddress(port), backlog).apply {
        executor = null // creates a default executor that runs on callers thread
        createContext("/", router)
    }

    override fun start() {
        server.start()
    }

    override fun stop() {
        server.stop(5)
    }

    override fun routing(config: HttpRoute.() -> Unit) {
        router.routing(config)
    }
}
