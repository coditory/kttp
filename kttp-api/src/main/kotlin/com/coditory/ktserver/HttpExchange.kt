package com.coditory.ktserver

import com.coditory.ktserver.http.HttpParams
import com.coditory.ktserver.http.HttpRequest
import com.coditory.ktserver.http.HttpResponseHead
import com.coditory.ktserver.http.HttpStatus
import com.coditory.ktserver.http.MutableHttpParams
import kotlinx.io.Sink
import kotlinx.io.writeString

data class HttpExchange(
    val request: HttpRequest,
    val responseBody: Sink,
) {
    val attributes = mutableMapOf<String, Any>()
    val responseHeaders = MutableHttpParams.empty()
    private var sentResponseHead = false

    suspend fun sendResponseHead(status: HttpStatus, headers: HttpParams = HttpParams.empty()) {
        sendResponseHead(HttpResponseHead(status, headers))
    }

    suspend fun sendResponseHead(response: HttpResponseHead) {
        require(!sentResponseHead) { "Response head was already sent" }
        responseBody.writeInt(response.status.code)
        responseBody.writeString("\n")
        response.headers.forEachEntry { name, value ->
            responseBody.writeString(name)
            responseBody.writeString(": ")
            responseBody.writeString(value)
            responseBody.writeString("\n")
        }
        sentResponseHead = true
    }
}
