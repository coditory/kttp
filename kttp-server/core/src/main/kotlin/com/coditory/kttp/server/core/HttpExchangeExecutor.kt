package com.coditory.kttp.server.core

import com.coditory.klog.Klog
import com.coditory.kttp.server.HttpErrorHandler
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpHandler
import com.coditory.kttp.server.HttpResponse
import com.coditory.kttp.server.HttpResponseSender
import com.coditory.kttp.server.HttpRouter

class HttpExchangeExecutor private constructor(
    private val responseSender: HttpResponseSender,
    private val errorHandler: HttpErrorHandler,
    private val router: HttpTreeRouter,
) : HttpRouter by router {
    constructor(
        responseSender: HttpResponseSender = HttpResponseSender.default(),
        errorHandler: HttpErrorHandler = HttpErrorHandler.default(),
        notFoundHandler: HttpHandler = NotFoundHttpHandler(),
    ) : this(responseSender, errorHandler, HttpTreeRouter(notFoundHandler))

    private val log = Klog.logger(HttpRouter::class)
    private val exchangeMonitor = HttpExchangeCounter()

    @Volatile
    private var stopped = false

    fun start() {
        stopped = false
    }

    fun stop() {
        stopped = true
    }

    fun stopped(): Boolean {
        return stopped
    }

    suspend fun stopAndWait() {
        stop()
        exchangeMonitor.waitForZero()
    }

    suspend fun execute(exchange: HttpExchange) {
        if (stopped) {
            log.warn { "Server is stopping. Skipping http exchange." }
            return
        }
        exchangeMonitor.increment()
        try {
            val chain = router.chain(exchange.request.toHead())
            val response = chain.doFilter(exchange)
            if (response !is HttpResponse.SentResponse) {
                responseSender.sendResponse(exchange, response)
            }
        } catch (e: Throwable) {
            log.error(e) { "Failed for exchange: ${exchange.request.method} ${exchange.request.uri}" }
            errorHandler.handle(exchange, e)
        } finally {
            exchangeMonitor.decrement()
        }
    }
}
