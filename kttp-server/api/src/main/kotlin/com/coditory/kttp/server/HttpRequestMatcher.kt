package com.coditory.kttp.server

import com.coditory.kttp.ContentType
import com.coditory.kttp.HttpRequestHead
import com.coditory.kttp.HttpRequestMethod

fun interface HttpRequestMatcher {
    fun matches(request: HttpRequestHead): Boolean

    companion object {
        fun from(
            method: HttpRequestMethod? = null,
            pathPattern: PathPattern? = null,
            consumes: ContentType? = null,
            produces: ContentType? = null,
        ): HttpRequestMatcher = StaticHttpRequestMatcher(
            method = method,
            pathPattern = pathPattern,
            consumes = consumes,
            produces = produces,
        )

        fun from(
            method: HttpRequestMethod? = null,
            pathPattern: String,
            consumes: ContentType? = null,
            produces: ContentType? = null,
        ): HttpRequestMatcher = StaticHttpRequestMatcher(
            method = method,
            pathPattern = pathPattern,
            consumes = consumes,
            produces = produces,
        )
    }
}

private data class StaticHttpRequestMatcher(
    val method: HttpRequestMethod? = null,
    val pathPattern: PathPattern? = null,
    val consumes: ContentType? = null,
    val produces: ContentType? = null,
) : HttpRequestMatcher {
    constructor(
        method: HttpRequestMethod? = null,
        pathPattern: String,
        consumes: ContentType? = null,
        produces: ContentType? = null,
    ) : this(
        method = method,
        pathPattern = PathPattern(pathPattern),
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
