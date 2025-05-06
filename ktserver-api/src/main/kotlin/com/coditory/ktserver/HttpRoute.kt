package com.coditory.ktserver

import com.coditory.ktserver.http.ContentType
import com.coditory.ktserver.http.HttpRequestMethod
import com.coditory.ktserver.http.PathPattern

interface HttpRoute {
    fun context(path: String, config: HttpRoute.() -> Unit)
    fun filter(filter: HttpFilter)
    fun handler(handler: HttpHandler)

    fun get(path: String, produces: ContentType? = null, consumes: ContentType? = null, action: HttpAction) {
        handler(SimpleHttpHandler(HttpRequestMethod.GET, PathPattern(path), consumes, produces, action))
    }

    fun post(path: String, produces: ContentType? = null, consumes: ContentType? = null, action: HttpAction) {
        handler(SimpleHttpHandler(HttpRequestMethod.POST, PathPattern(path), consumes, produces, action))
    }

    fun put(path: String, produces: ContentType? = null, consumes: ContentType? = null, action: HttpAction) {
        handler(SimpleHttpHandler(HttpRequestMethod.PUT, PathPattern(path), consumes, produces, action))
    }

    fun delete(path: String, produces: ContentType? = null, consumes: ContentType? = null, action: HttpAction) {
        handler(SimpleHttpHandler(HttpRequestMethod.DELETE, PathPattern(path), consumes, produces, action))
    }
}
