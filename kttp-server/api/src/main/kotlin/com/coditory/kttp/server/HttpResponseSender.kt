package com.coditory.kttp.server

import com.coditory.kttp.headers.ContentType
import com.coditory.kttp.headers.HttpHeaders
import com.coditory.kttp.serialization.HttpSerDeserializer
import com.coditory.kttp.serialization.Serializer

interface HttpResponseSender {
    suspend fun sendResponse(exchange: HttpExchange, response: HttpResponse)

    companion object {
        private val DEFAULT = SerializingHttpResponseSender(HttpSerDeserializer.default())

        fun default(): HttpResponseSender = DEFAULT

        fun default(serializer: Serializer): HttpResponseSender {
            if (serializer == HttpSerDeserializer.default()) return DEFAULT
            return SerializingHttpResponseSender(serializer)
        }
    }
}

private class SerializingHttpResponseSender(
    private val serializer: Serializer,
) : HttpResponseSender {
    @Suppress("UNCHECKED_CAST")
    override suspend fun sendResponse(exchange: HttpExchange, response: HttpResponse) {
        when (response) {
            is HttpResponse.SentResponse -> {
                // Response already sent. Nothing to do here.
            }

            is HttpResponse.StatusResponse -> {
                val headers = HttpHeaders.from(
                    HttpHeaders.ContentLength to "0",
                )
                exchange.sendResponseHead(response.status, headers)
            }

            is HttpResponse.SerializableResponse<*> -> {
                val resp = response as HttpResponse.SerializableResponse<Any>
                val req = exchange.request.toHead()
                val body = serializer.serializeToString(resp.body, resp.serializer, req).toByteArray()
                val headers = HttpHeaders.from(
                    HttpHeaders.ContentType to ContentType.Application.Json.value,
                    HttpHeaders.ContentLength to body.size.toString(),
                )
                exchange.sendResponseHead(response.status, headers)
                exchange.responseBody.use { out ->
                    out.write(body)
                }
            }

            is HttpResponse.TextResponse -> {
                val body = response.body.toByteArray()
                val headers = HttpHeaders.from(
                    HttpHeaders.ContentType to ContentType.Text.Plain.value,
                    HttpHeaders.ContentLength to body.size.toString(),
                )
                exchange.sendResponseHead(response.status, headers)
                exchange.responseBody.use { out ->
                    out.write(body)
                }
            }
        }
    }
}
