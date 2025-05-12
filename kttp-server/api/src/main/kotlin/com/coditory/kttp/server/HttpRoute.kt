package com.coditory.kttp.server

import com.coditory.kttp.HttpRequestMethod
import com.coditory.kttp.headers.ContentType

interface HttpRoute {
    fun routing(path: String, config: HttpRoute.() -> Unit)
    fun filter(filter: HttpFilter)
    fun handler(handler: HttpHandler)

    fun get(path: String, produces: ContentType? = null, consumes: ContentType? = null, action: HttpHandlerAction) {
        handler(
            HttpHandler(
                method = HttpRequestMethod.GET,
                pathPattern = PathPattern(path),
                consumes = consumes,
                produces = produces,
                action = action,
            ),
        )
    }

    fun post(path: String, produces: ContentType? = null, consumes: ContentType? = null, action: HttpHandlerAction) {
        handler(
            HttpHandler(
                method = HttpRequestMethod.POST,
                pathPattern = PathPattern(path),
                consumes = consumes,
                produces = produces,
                action = action,
            ),
        )
    }

    fun put(path: String, produces: ContentType? = null, consumes: ContentType? = null, action: HttpHandlerAction) {
        handler(
            HttpHandler(
                method = HttpRequestMethod.PUT,
                pathPattern = PathPattern(path),
                consumes = consumes,
                produces = produces,
                action = action,
            ),
        )
    }

    fun delete(path: String, produces: ContentType? = null, consumes: ContentType? = null, action: HttpHandlerAction) {
        handler(
            HttpHandler(
                method = HttpRequestMethod.DELETE,
                pathPattern = PathPattern(path),
                consumes = consumes,
                produces = produces,
                action = action,
            ),
        )
    }
}
