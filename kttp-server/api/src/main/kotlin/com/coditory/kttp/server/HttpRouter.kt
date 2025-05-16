package com.coditory.kttp.server

import com.coditory.kttp.HttpRequestHead

interface HttpRouter : HttpRoute {
    fun removeHandler(handler: HttpHandler)
    fun removeFilter(filter: HttpFilter)
    fun chain(request: HttpRequestHead): HttpChain
}
