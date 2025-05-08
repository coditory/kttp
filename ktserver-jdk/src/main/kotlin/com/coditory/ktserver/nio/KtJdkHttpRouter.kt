package com.coditory.ktserver.nio

import com.coditory.klog.Klog
import com.coditory.ktserver.HttpCompositeRouter
import com.coditory.ktserver.HttpErrorHandler
import com.coditory.ktserver.HttpExchange
import com.coditory.ktserver.HttpHandler
import com.coditory.ktserver.HttpRoute
import com.coditory.ktserver.HttpSerDeserializer
import com.coditory.ktserver.NotFoundHttpHandler
import com.coditory.ktserver.http.HttpParams
import com.coditory.ktserver.http.HttpRequest
import com.coditory.ktserver.http.HttpRequestMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import com.sun.net.httpserver.HttpExchange as JdkHttpExchange
import com.sun.net.httpserver.HttpHandler as JdkHttpHandler

@OptIn(DelicateCoroutinesApi::class)
internal class KtJdkHttpRouter(
    private val requestScope: CoroutineScope,
    private val responseSendingScope: CoroutineScope,
    private val serde: HttpSerDeserializer,
    notFoundAction: HttpHandler = NotFoundHttpHandler(),
    errorHandler: HttpErrorHandler = HttpErrorHandler.default(),
) : JdkHttpHandler {
    private val log = Klog.logger(KtJdkHttpRouter::class)
    private val router = HttpCompositeRouter(
        notFoundAction = notFoundAction,
        errorHandler = errorHandler,
        responseSender = KtJdkHttpResponseSender(serde, responseSendingScope),
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
