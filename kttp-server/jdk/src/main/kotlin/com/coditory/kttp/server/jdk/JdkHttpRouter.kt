package com.coditory.kttp.server.jdk

import com.coditory.klog.Klog
import com.coditory.kttp.HttpParams
import com.coditory.kttp.HttpRequestMethod
import com.coditory.kttp.serialization.SerDeserializer
import com.coditory.kttp.server.HttpErrorHandler
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpHandlerAction
import com.coditory.kttp.server.HttpRequest
import com.coditory.kttp.server.HttpRoute
import com.coditory.kttp.server.core.HttpCompositeRouter
import com.coditory.kttp.server.core.NotFoundHttpHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import com.sun.net.httpserver.HttpExchange as JdkHttpExchange
import com.sun.net.httpserver.HttpHandler as JdkHttpHandler

@OptIn(DelicateCoroutinesApi::class)
internal class JdkHttpRouter(
    private val requestScope: CoroutineScope,
    private val responseSendingScope: CoroutineScope,
    private val serde: SerDeserializer,
    notFoundAction: HttpHandlerAction = NotFoundHttpHandler(),
    errorHandler: HttpErrorHandler = HttpErrorHandler.default(),
) : JdkHttpHandler {
    private val log = Klog.logger(JdkHttpRouter::class)
    private val router = HttpCompositeRouter(
        notFoundAction = notFoundAction,
        errorHandler = errorHandler,
        responseSender = JdkHttpResponseSender(serde, responseSendingScope),
    )

    fun routing(config: HttpRoute.() -> Unit) = router.routing(config)

    override fun handle(jdkExchange: JdkHttpExchange) {
        requestScope.launch {
            handleAndCatch(jdkExchange)
        }
    }

    private suspend fun handleAndCatch(jdkExchange: JdkHttpExchange) {
        try {
            val exchange = buildExchange(jdkExchange)
            router.handle(exchange)
        } catch (e: Throwable) {
            log.error(e) { "Failed for exchange: ${jdkExchange.requestMethod} ${jdkExchange.requestURI}" }
            jdkExchange.sendResponseHeaders(500, 0)
        }
    }

    private fun buildExchange(jdkExchange: JdkHttpExchange): HttpExchange {
        val request = HttpRequest(
            method = HttpRequestMethod.valueOf(jdkExchange.requestMethod),
            uri = jdkExchange.requestURI,
            headers = HttpParams.fromMultiMap(jdkExchange.requestHeaders.toMap()),
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
