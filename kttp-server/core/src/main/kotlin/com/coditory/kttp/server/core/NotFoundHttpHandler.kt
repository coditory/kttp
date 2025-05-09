package com.coditory.kttp.server.core

import com.coditory.kttp.HttpStatus
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpHandlerAction
import com.coditory.kttp.server.HttpResponse

class NotFoundHttpHandler : HttpHandlerAction {
    override suspend fun handle(exchange: HttpExchange): HttpResponse {
        return HttpResponse.StatusResponse(HttpStatus.NotFound)
    }
}
