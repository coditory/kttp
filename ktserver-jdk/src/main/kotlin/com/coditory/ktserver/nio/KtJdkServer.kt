package com.coditory.ktserver.nio

import com.coditory.ktserver.HttpAction
import com.coditory.ktserver.HttpErrorHandler
import com.coditory.ktserver.HttpRoute
import com.coditory.ktserver.HttpSerDeserializer
import com.coditory.ktserver.HttpServer
import com.coditory.ktserver.NotFoundHttpAction
import com.coditory.ktserver.serialization.ScoredHttpSerDeserializer
import com.coditory.quark.uri.Ports
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.net.InetSocketAddress
import com.sun.net.httpserver.HttpServer as JdkHttpServer

class KtJdkServer(
    val port: Int = Ports.getNextAvailable(),
    backlog: Int = 0,
    requestScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    serde: HttpSerDeserializer = ScoredHttpSerDeserializer.default(),
    notFoundAction: HttpAction = NotFoundHttpAction(),
    errorHandler: HttpErrorHandler = HttpErrorHandler.default(),
) : HttpServer {
    private val router = KtJdkHttpRouter(
        requestScope = requestScope,
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
