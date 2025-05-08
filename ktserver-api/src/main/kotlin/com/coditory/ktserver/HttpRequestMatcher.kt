package com.coditory.ktserver

import com.coditory.ktserver.http.ContentType
import com.coditory.ktserver.http.HttpRequestHead
import com.coditory.ktserver.http.HttpRequestMethod
import com.coditory.ktserver.http.PathPattern

fun interface HttpRequestMatcher {
    fun matches(request: HttpRequestHead): Boolean
}

internal data class HttpSimpleRequestMatcher(
    val method: HttpRequestMethod? = null,
    val pathPattern: PathPattern? = null,
    val consumes: ContentType? = null,
    val produces: ContentType? = null,
) : HttpRequestMatcher {
    constructor(
        method: HttpRequestMethod? = null,
        path: String,
        consumes: ContentType? = null,
        produces: ContentType? = null,
    ) : this(
        method = method,
        pathPattern = PathPattern(path),
        consumes = consumes,
        produces = produces,
    )

    override fun matches(request: HttpRequestHead): Boolean {
        if (method != null && method != request.method) return false
        if (pathPattern != null && !pathPattern.matches(request.uri.path)) return false
        if (consumes == null && produces == null) return true
        // TODO: Negotiate content with Content-Type and Accept
        return true
    }
}
