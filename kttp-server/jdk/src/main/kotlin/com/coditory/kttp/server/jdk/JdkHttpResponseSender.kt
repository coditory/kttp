package com.coditory.kttp.server.jdk

import com.coditory.kttp.ContentType
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
                    jdkExchange.sendResponseHeaders(response.status.code, 0.toLong())
                }
            }

            is HttpResponse.SerializableResponse<*> -> {
                val resp = response as HttpResponse.SerializableResponse<Any>
                val req = exchange.request.toHead()
                val body = serializer.serializeToString(resp.body, resp.serializer, req).toByteArray()
                writeScope.launch {
                    jdkExchange.responseHeaders.add("Content-Type", ContentType.Application.Json.value)
                    jdkExchange.sendResponseHeaders(response.status.code, body.size.toLong())
                    jdkExchange.responseBody.use { out ->
                        out.write(body)
                    }
                }
            }

            is HttpResponse.TextResponse -> {
                val body = response.body.toByteArray()
                jdkExchange.responseHeaders.add("Content-Type", ContentType.Text.Plain.value)
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
