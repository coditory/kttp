package com.coditory.kttp.server

import com.coditory.kttp.HttpResponseHead
import com.coditory.kttp.HttpStatus
import com.coditory.kttp.headers.HttpHeaders
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.Sink
import kotlinx.io.writeString
import java.util.concurrent.ConcurrentHashMap

data class HttpExchange(
    val request: HttpRequest,
    val responseBody: Sink,
) {
    val attributes = ConcurrentHashMap<String, Any>()
    private val mutex = Mutex()
    private var sent = false

    suspend fun sendResponseHead(status: HttpStatus, headers: HttpHeaders = HttpHeaders.empty()) {
        sendResponseHead(HttpResponseHead(status, headers))
    }

    suspend fun sendResponseHead(response: HttpResponseHead) {
        mutex.withLock {
            require(!sent) { "Response head was already sent" }
            responseBody.writeInt(response.status.code)
            responseBody.writeString("\n")
            sent = true
            response.headers.forEachEntry { name, value ->
                responseBody.writeString(name)
                responseBody.writeString(": ")
                responseBody.writeString(value)
                responseBody.writeString("\n")
            }
        }
    }
}
