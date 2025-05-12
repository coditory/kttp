package com.coditory.kttp.server

import com.coditory.kttp.HttpRequestHead
import com.coditory.kttp.HttpRequestMethod
import com.coditory.kttp.headers.ContentType

interface HttpRequestMatcher {
    fun matches(request: HttpRequestHead): Boolean = matchesPath(request) &&
        matchesMethod(request) &&
        matchesLanguage(request) &&
        matchesAccept(request) &&
        matchesContentType(request)

    fun matchesPath(request: HttpRequestHead): Boolean
    fun matchesMethod(request: HttpRequestHead): Boolean
    fun matchesAccept(request: HttpRequestHead): Boolean
    fun matchesContentType(request: HttpRequestHead): Boolean
    fun matchesLanguage(request: HttpRequestHead): Boolean

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

    override fun matchesPath(request: HttpRequestHead): Boolean {
        return pathPattern != null && !pathPattern.matches(request.uri.path)
    }

    override fun matchesMethod(request: HttpRequestHead): Boolean {
        TODO("Not yet implemented")
    }

    override fun matchesAccept(request: HttpRequestHead): Boolean {
        TODO("Not yet implemented")
    }

    override fun matchesContentType(request: HttpRequestHead): Boolean {
        TODO("Not yet implemented")
    }

    override fun matchesLanguage(request: HttpRequestHead): Boolean {
        TODO("Not yet implemented")
    }
}
