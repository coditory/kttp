package com.coditory.kttp.server

import com.coditory.kttp.HttpRequestHead
import kotlin.reflect.KClass

interface HttpRouter : HttpRoute {
    fun hasHandler(request: HttpRequestHead): Boolean
    fun getRequestMatchers(path: String): List<HttpRequestMatcher>
    fun removeHandler(handler: KClass<HttpHandler>)
    fun removeHandler(handler: HttpHandler)
    fun removeFilter(filter: KClass<HttpFilter>)
    fun removeFilter(filter: HttpFilter)
    fun chain(request: HttpRequestHead): HttpChain
}
