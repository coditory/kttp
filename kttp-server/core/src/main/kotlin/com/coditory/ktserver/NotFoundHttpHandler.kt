package com.coditory.ktserver

import com.coditory.ktserver.http.HttpResponse
import com.coditory.ktserver.http.HttpStatus

class NotFoundHttpHandler : HttpHandler {
    override suspend fun handle(exchange: HttpExchange): HttpResponse {
        return HttpResponse.StatusResponse(HttpStatus.NotFound)
    }
}
