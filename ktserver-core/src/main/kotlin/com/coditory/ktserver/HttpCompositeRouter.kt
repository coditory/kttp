package com.coditory.ktserver

import com.coditory.klog.Klog
import com.coditory.ktserver.http.HttpRequest
import com.coditory.ktserver.http.HttpResponse
import com.coditory.ktserver.http.HttpStatus
import com.coditory.ktserver.serialization.ScoredHttpSerDeserializer
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
class HttpCompositeRouter(
    notFoundAction: HttpHandler = NotFoundHttpHandler(),
    private val responseSender: HttpResponseSender = DefaultHttpResponseSender(ScoredHttpSerDeserializer.default()),
    private val errorHandler: HttpErrorHandler = HttpErrorHandler.default(),
) {
    private val log = Klog.logger(HttpCompositeRouter::class)
    private val notFoundChain = HttpHandlerChain(notFoundAction)
    private val rootRoute = HttpRouteNode()

    fun routing(config: HttpRoute.() -> Unit) {
        with(rootRoute, config)
    }

    suspend fun handle(exchange: HttpExchange) {
        val nodes = rootRoute.aggregate(exchange.request.uri.path)
        val handlerChain = buildHandlerChain(nodes, exchange.request)
        val chain = buildFilterChain(nodes, handlerChain)
        handle(chain, exchange)
    }

    private suspend fun handle(chain: HttpChain, exchange: HttpExchange) {
        val response = try {
            chain.doFilter(exchange)
        } catch (e: Throwable) {
            log.error(e) { "Failed for exchange: ${exchange.request.method} ${exchange.request.uri}" }
            errorHandler.handle(exchange, e)
        }
        if (response is HttpResponse.SentResponse) return
        try {
            responseSender.sendResponse(exchange, response)
        } catch (e: Throwable) {
            log.error(e) { "Failed sending response for: ${exchange.request.method} ${exchange.request.uri}" }
            responseSender.sendResponse(exchange, HttpResponse.StatusResponse(HttpStatus.InternalServerError))
        }
    }

    private fun buildFilterChain(nodes: List<HttpRouteNode>, terminating: HttpChain): HttpChain {
        var next = terminating
        for (i in nodes.size - 1 downTo 0) {
            val node = nodes[i]
            for (filter in node.getFilters()) {
                val chain = HttpFilterChain(filter, next)
                next = chain
            }
        }
        return terminating
    }

    private fun buildHandlerChain(nodes: List<HttpRouteNode>, request: HttpRequest): HttpChain {
        for (i in nodes.size - 1 downTo 0) {
            val node = nodes[i]
            val handler = node.getMatchingHandler(request)
            if (handler != null) {
                return HttpHandlerChain(handler)
            }
        }
        return notFoundChain
    }
}
