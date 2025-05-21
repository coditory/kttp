package com.coditory.kttp.server.jdk

import com.coditory.kttp.headers.HttpHeaders
import com.coditory.kttp.headers.MediaType
import com.coditory.kttp.serialization.Serializer
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpResponse
import com.coditory.kttp.server.HttpResponseSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.sun.net.httpserver.HttpExchange as JdkHttpExchange

class JdkHttpResponseSender(
    private val serializer: Serializer,
    private val writeScope: CoroutineScope,
) : HttpResponseSender {
    @Suppress("UNCHECKED_CAST")
    override suspend fun sendResponse(
        exchange: HttpExchange,
        response: HttpResponse,
    ) {
        val jdkExchange = exchange.attributes["jdkExchange"]!! as JdkHttpExchange
        when (response) {
            is HttpResponse.SentResponse -> {
                // Response already sent. Nothing to do here.
            }

            is HttpResponse.StatusResponse -> {
                writeScope.launch {
                    response.headers.forEachEntry { name, value ->
                        jdkExchange.responseHeaders.add(name, value)
                    }
                    jdkExchange.sendResponseHeaders(response.status.code, -1L)
                }
            }

            is HttpResponse.SerializableResponse<*> -> {
                val resp = response as HttpResponse.SerializableResponse<Any>
                val req = exchange.request.toHead()
                val body = serializer.serializeToString(resp.body, resp.serializer, req).toByteArray()
                writeScope.launch {
                    response.headers.forEachEntry { name, value ->
                        jdkExchange.responseHeaders.add(name, value)
                    }
                    if (!response.headers.contains(HttpHeaders.ContentType)) {
                        jdkExchange.responseHeaders.add(HttpHeaders.ContentType, MediaType.Application.Json.value)
                    }
                    jdkExchange.sendResponseHeaders(response.status.code, body.size.toLong())
                    jdkExchange.responseBody.use { out ->
                        out.write(body)
                    }
                }
            }

            is HttpResponse.TextResponse -> {
                val body = response.body.toByteArray()
                response.headers.forEachEntry { name, value ->
                    jdkExchange.responseHeaders.add(name, value)
                }
                if (!response.headers.contains(HttpHeaders.ContentType)) {
                    jdkExchange.responseHeaders.add(HttpHeaders.ContentType, MediaType.Text.Plain.value)
                }
                jdkExchange.sendResponseHeaders(response.status.code, body.size.toLong())
                writeScope.launch {
                    exchange.responseBody.use { out ->
                        out.write(body)
                    }
                }
            }
        }
    }
}
