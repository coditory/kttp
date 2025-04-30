package com.coditory.kttp.server

import com.coditory.kttp.HttpRequestMethod
import com.coditory.kttp.headers.MediaType

interface HttpRoute {
    fun routing(config: HttpRoute.() -> Unit) = routing(HttpRequestMatcher.matchingAll(), config)

    fun routing(matcher: HttpRequestMatcher, config: HttpRoute.() -> Unit)

    fun routing(
        path: String? = null,
        method: HttpRequestMethod? = null,
        produces: MediaType? = null,
        consumes: MediaType? = null,
        predicate: HttpRequestPredicate? = null,
        config: HttpRoute.() -> Unit,
    ) {
        val matcher = HttpRequestMatcher.from(
            methods = method?.let { setOf(it) } ?: emptySet(),
            pathPattern = path?.let { HttpPathPattern.from(it) },
            consumes = consumes?.let { setOf(it) } ?: emptySet(),
            produces = produces?.let { setOf(it) } ?: emptySet(),
            predicate = predicate ?: HttpRequestPredicate.acceptsAll(),
        )
        routing(matcher, config)
    }

    fun filter(filter: HttpFilter) = filter(HttpRequestMatcher.matchingAll(), filter)

    fun filter(matcher: HttpRequestMatcher, filter: HttpFilter)

    fun filter(path: String? = null, method: HttpRequestMethod? = null, produces: MediaType? = null, consumes: MediaType? = null, predicate: HttpRequestPredicate? = null, filter: HttpFilter) {
        val matcher = HttpRequestMatcher.from(
            methods = method?.let { setOf(it) } ?: emptySet(),
            pathPattern = path?.let { HttpPathPattern.from(it) },
            consumes = consumes?.let { setOf(it) } ?: emptySet(),
            produces = produces?.let { setOf(it) } ?: emptySet(),
            predicate = predicate ?: HttpRequestPredicate.acceptsAll(),
        )
        filter(matcher, filter)
    }

    fun handler(handler: HttpHandler) = handler(HttpRequestMatcher.matchingAll(), handler)

    fun handler(matcher: HttpRequestMatcher, handler: HttpHandler)

    fun handler(path: String? = null, method: HttpRequestMethod? = null, produces: MediaType? = null, consumes: MediaType? = null, predicate: HttpRequestPredicate? = null, action: HttpHandler) {
        val matcher = HttpRequestMatcher.from(
            methods = method?.let { setOf(it) } ?: emptySet(),
            pathPattern = path?.let { HttpPathPattern.from(it) },
            consumes = consumes?.let { setOf(it) } ?: emptySet(),
            produces = produces?.let { setOf(it) } ?: emptySet(),
            predicate = predicate ?: HttpRequestPredicate.acceptsAll(),
        )
        handler(matcher, action)
    }

    fun get(path: String, produces: MediaType? = null, consumes: MediaType? = null, predicate: HttpRequestPredicate? = null, action: HttpHandler) {
        handler(path, HttpRequestMethod.GET, produces, consumes, predicate, action)
    }

    fun post(path: String, produces: MediaType? = null, consumes: MediaType? = null, predicate: HttpRequestPredicate? = null, action: HttpHandler) {
        handler(path, HttpRequestMethod.POST, produces, consumes, predicate, action)
    }

    fun put(path: String, produces: MediaType? = null, consumes: MediaType? = null, predicate: HttpRequestPredicate? = null, action: HttpHandler) {
        handler(path, HttpRequestMethod.PUT, produces, consumes, predicate, action)
    }

    fun delete(path: String, produces: MediaType? = null, consumes: MediaType? = null, predicate: HttpRequestPredicate? = null, action: HttpHandler) {
        handler(path, HttpRequestMethod.DELETE, produces, consumes, predicate, action)
    }

    fun patch(path: String, produces: MediaType? = null, consumes: MediaType? = null, predicate: HttpRequestPredicate? = null, action: HttpHandler) {
        handler(path, HttpRequestMethod.PATCH, produces, consumes, predicate, action)
    }

    fun head(path: String, produces: MediaType? = null, consumes: MediaType? = null, predicate: HttpRequestPredicate? = null, action: HttpHandler) {
        handler(path, HttpRequestMethod.HEAD, produces, consumes, predicate, action)
    }

    fun options(path: String, produces: MediaType? = null, consumes: MediaType? = null, predicate: HttpRequestPredicate? = null, action: HttpHandler) {
        handler(path, HttpRequestMethod.OPTIONS, produces, consumes, predicate, action)
    }
}
