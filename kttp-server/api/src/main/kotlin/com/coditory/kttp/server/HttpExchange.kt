package com.coditory.kttp.server

import com.coditory.kttp.HttpParams
import com.coditory.kttp.HttpResponseHead
import com.coditory.kttp.HttpStatus
import kotlinx.io.Sink
import kotlinx.io.writeString

data class HttpExchange(
    val request: HttpRequest,
    val responseBody: Sink,
) {
    val attributes = mutableMapOf<String, Any>()
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
