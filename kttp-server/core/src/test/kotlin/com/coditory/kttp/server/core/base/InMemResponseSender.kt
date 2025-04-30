package com.coditory.kttp.server.core.base

import com.coditory.kttp.serialization.SerDeserializer
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpResponse
import com.coditory.kttp.server.HttpResponseSender

class InMemResponseSender(
    private val serde: SerDeserializer,
) : HttpResponseSender {
    override suspend fun sendResponse(
        exchange: HttpExchange,
        response: HttpResponse,
    ) {
        TODO("Not yet implemented")
    }
}
