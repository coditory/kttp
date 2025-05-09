package com.coditory.kttp.server.core

import com.coditory.kttp.ContentType
import com.coditory.kttp.HttpParams
import com.coditory.kttp.serialization.Serializer
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpResponse
import com.coditory.kttp.server.HttpResponseSender

internal class DefaultHttpResponseSender(
    private val serializer: Serializer,
) : HttpResponseSender {
    @Suppress("UNCHECKED_CAST")
    override suspend fun sendResponse(exchange: HttpExchange, response: HttpResponse) {
        when (response) {
            is HttpResponse.SentResponse -> {
                // Response already sent. Nothing to do here.
            }

            is HttpResponse.StatusResponse -> {
                val headers = HttpParams.fromMap(
                    mapOf(
                        "Content-Length" to "0",
                    ),
                )
                exchange.sendResponseHead(response.status, headers)
            }

            is HttpResponse.SerializableResponse<*> -> {
                val resp = response as HttpResponse.SerializableResponse<Any>
                val req = exchange.request.toHead()
                val body = serializer.serializeToString(resp.body, resp.serializer, req).toByteArray()
                val headers = HttpParams.fromMap(
                    mapOf(
                        "Content-Type" to ContentType.Application.Json.value,
                        "Content-Length" to body.size.toString(),
                    ),
                )
                exchange.sendResponseHead(response.status, headers)
                exchange.responseBody.use { out ->
                    out.write(body)
                }
            }

            is HttpResponse.TextResponse -> {
                val body = response.body.toByteArray()
                val headers = HttpParams.fromMap(
                    mapOf(
                        "Content-Type" to ContentType.Text.Plain.value,
                        "Content-Length" to body.size.toString(),
                    ),
                )
                exchange.sendResponseHead(response.status, headers)
                exchange.responseBody.use { out ->
                    out.write(body)
                }
            }
        }
    }
}
