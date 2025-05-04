package com.coditory.ktserver

import com.coditory.ktserver.http.HttpRequestMethod

interface HttpRoute {
    fun context(path: String, config: HttpRoute.() -> Unit)
    fun filter(filter: HttpFilter)
    fun handler(handler: HttpHandler)

    fun get(path: String, produces: String?, action: HttpAction) {
        handler(HttpHandler(HttpRequestMethod.GET, path, produces, action))
    }

    fun post(path: String, produces: String?, action: HttpAction) {
        handler(AsyncHttpHandler(HttpRequestMethod.POST, path, produces, action))
    }

    fun put(path: String, produces: String?, action: HttpAction) {
        handler(AsyncHttpHandler(HttpRequestMethod.PUT, path, produces, action))
    }

    fun delete(path: String, produces: String?, action: HttpAction) {
        handler(AsyncHttpHandler(HttpRequestMethod.DELETE, path, produces, action))
    }
}
