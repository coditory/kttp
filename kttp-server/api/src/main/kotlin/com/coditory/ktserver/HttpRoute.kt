package com.coditory.ktserver

import com.coditory.ktserver.http.ContentType
import com.coditory.ktserver.http.HttpRequestMethod
import com.coditory.ktserver.http.PathPattern

interface HttpRoute {
    fun routing(path: String, config: HttpRoute.() -> Unit)
    fun filter(filter: HttpFilter)
    fun handler(handler: HttpMatchingHandler)

    fun get(path: String, produces: ContentType? = null, consumes: ContentType? = null, action: HttpHandler) {
        handler(SimpleHttpMatchingHandler(HttpRequestMethod.GET, PathPattern(path), consumes, produces, action))
    }

    fun post(path: String, produces: ContentType? = null, consumes: ContentType? = null, action: HttpHandler) {
        handler(SimpleHttpMatchingHandler(HttpRequestMethod.POST, PathPattern(path), consumes, produces, action))
    }

    fun put(path: String, produces: ContentType? = null, consumes: ContentType? = null, action: HttpHandler) {
        handler(SimpleHttpMatchingHandler(HttpRequestMethod.PUT, PathPattern(path), consumes, produces, action))
    }

    fun delete(path: String, produces: ContentType? = null, consumes: ContentType? = null, action: HttpHandler) {
        handler(SimpleHttpMatchingHandler(HttpRequestMethod.DELETE, PathPattern(path), consumes, produces, action))
    }
}
