package com.coditory.ktserver.nio

import com.coditory.ktserver.HttpExchange
import com.coditory.ktserver.HttpResponseSender
import com.coditory.ktserver.HttpSerializer
import com.coditory.ktserver.http.ContentType
import com.coditory.ktserver.http.HttpResponse
import com.sun.net.httpserver.HttpExchange as JdkHttpExchange

class KtJdkHttpResponseSender(
    private val serializer: HttpSerializer,
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
                jdkExchange.sendResponseHeaders(response.status.code, 0.toLong())
            }

            is HttpResponse.SerializableResponse<*> -> {
                val resp = response as HttpResponse.SerializableResponse<Any>
                val req = exchange.request.toHttpRequestHead()
                val body = serializer.serializeToString(resp.body, resp.serializer, req).toByteArray()
                jdkExchange.responseHeaders.add("Content-Type", ContentType.Application.Json.value)
                jdkExchange.sendResponseHeaders(response.status.code, body.size.toLong())
                jdkExchange.responseBody.use { out ->
                    out.write(body)
                }
            }

            is HttpResponse.TextResponse -> {
                val body = response.body.toByteArray()
                jdkExchange.responseHeaders.add("Content-Type", ContentType.Text.Plain.value)
                jdkExchange.sendResponseHeaders(response.status.code, body.size.toLong())
                exchange.responseBody.use { out ->
                    out.write(body)
                }
            }
        }
    }
}
