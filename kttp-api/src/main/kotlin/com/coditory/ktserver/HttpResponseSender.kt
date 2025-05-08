package com.coditory.ktserver

import com.coditory.ktserver.http.HttpResponse

interface HttpResponseSender {
    suspend fun sendResponse(exchange: HttpExchange, response: HttpResponse)
}
