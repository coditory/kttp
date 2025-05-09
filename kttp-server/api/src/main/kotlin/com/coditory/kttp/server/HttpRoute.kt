package com.coditory.kttp.server

import com.coditory.kttp.ContentType
import com.coditory.kttp.HttpRequestMethod

interface HttpRoute {
    fun routing(path: String, config: HttpRoute.() -> Unit)
    fun filter(filter: HttpFilter)
    fun handler(handler: HttpHandler)

    fun get(path: String, produces: ContentType? = null, consumes: ContentType? = null, action: HttpHandlerAction) {
        val matcher = HttpRequestMatcher.from(
            method = HttpRequestMethod.GET,
            pathPattern = path,
            consumes = consumes,
            produces = produces,
        )
        handler(HttpHandler.matching(matcher, action))
    }

    fun post(path: String, produces: ContentType? = null, consumes: ContentType? = null, action: HttpHandlerAction) {
        val matcher = HttpRequestMatcher.from(
            method = HttpRequestMethod.POST,
            pathPattern = path,
            consumes = consumes,
            produces = produces,
        )
        handler(HttpHandler.matching(matcher, action))
    }

    fun put(path: String, produces: ContentType? = null, consumes: ContentType? = null, action: HttpHandlerAction) {
        val matcher = HttpRequestMatcher.from(
            method = HttpRequestMethod.PUT,
            pathPattern = path,
            consumes = consumes,
            produces = produces,
        )
        handler(HttpHandler.matching(matcher, action))
    }

    fun delete(path: String, produces: ContentType? = null, consumes: ContentType? = null, action: HttpHandlerAction) {
        val matcher = HttpRequestMatcher.from(
            method = HttpRequestMethod.DELETE,
            pathPattern = path,
            consumes = consumes,
            produces = produces,
        )
        handler(HttpHandler.matching(matcher, action))
    }
}
