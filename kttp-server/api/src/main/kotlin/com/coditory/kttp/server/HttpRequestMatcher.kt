package com.coditory.kttp.server

import com.coditory.kttp.HttpRequestHead
import com.coditory.kttp.HttpRequestMethod
import com.coditory.kttp.headers.Accept
import com.coditory.kttp.headers.ContentType

fun interface HttpRequestPredicate {
    fun accepts(requestHead: HttpRequestHead): Boolean

    companion object {
        private val ACCEPTS_ALL = HttpRequestPredicate { true }
        internal fun acceptsAll(): HttpRequestPredicate = ACCEPTS_ALL
    }
}

interface HttpRequestMatcher {
    val methods: Set<HttpRequestMethod>
    val pathPattern: HttpPathPattern?
    val consumes: Set<ContentType>
    val produces: Set<ContentType>
    val predicate: HttpRequestPredicate

    fun matches(request: HttpRequestHead): Boolean = matchesPath(request.uri.path) &&
        matchesMethod(request.method) &&
        matchesAccept(request.headers.accept()) &&
        matchesContentType(request.headers.contentType()) &&
        predicate.accepts(request)

    fun matchesPath(path: String): Boolean
    fun matchesMethod(method: HttpRequestMethod): Boolean
    fun matchesAccept(accept: Accept?): Boolean
    fun matchesContentType(contentType: ContentType?): Boolean
    fun subMatcher(matcher: HttpRequestMatcher): HttpRequestMatcher

    companion object {
        private val MATCHING_ALL = HttpStaticRequestMatcher(
            pathPattern = null,
            methods = emptySet(),
            consumes = emptySet(),
            produces = emptySet(),
            predicate = HttpRequestPredicate.acceptsAll(),
        )

        fun matchingAll(): HttpRequestMatcher = MATCHING_ALL

        fun from(
            pathPattern: HttpPathPattern? = null,
            methods: Set<HttpRequestMethod> = emptySet(),
            consumes: Set<ContentType> = emptySet(),
            produces: Set<ContentType> = emptySet(),
            predicate: HttpRequestPredicate = HttpRequestPredicate.acceptsAll(),
        ): HttpRequestMatcher = HttpStaticRequestMatcher(
            methods = methods,
            pathPattern = pathPattern,
            consumes = ContentType.minimize(consumes),
            produces = ContentType.minimize(produces),
            predicate = predicate,
        )
    }
}

private data class HttpStaticRequestMatcher(
    override val methods: Set<HttpRequestMethod>,
    override val pathPattern: HttpPathPattern?,
    override val consumes: Set<ContentType>,
    override val produces: Set<ContentType>,
    override val predicate: HttpRequestPredicate,
) : HttpRequestMatcher {
    override fun matchesPath(path: String): Boolean {
        return this.pathPattern == null || this.pathPattern.matches(path)
    }

    override fun matchesMethod(method: HttpRequestMethod): Boolean {
        return this.methods.isEmpty() || this.methods.contains(method)
    }

    override fun matchesAccept(accept: Accept?): Boolean {
        return accept == null || this.produces.isEmpty() || this.produces.any { accept.matches(it) }
    }

    override fun matchesContentType(contentType: ContentType?): Boolean {
        return contentType == null || this.consumes.isEmpty() || this.consumes.any { it.matches(contentType) }
    }

    override fun subMatcher(matcher: HttpRequestMatcher): HttpRequestMatcher {
        val otherPath = matcher.pathPattern
        val otherMethods = matcher.methods
        if (this.methods.isNotEmpty()) {
            val conflictingMethods = otherMethods.filter { !this.methods.contains(it) }
            require(conflictingMethods.isEmpty()) { "Conflicting methods: $conflictingMethods" }
        }
        val otherConsumes = matcher.consumes
        if (this.consumes.isNotEmpty()) {
            val conflictingConsumes = otherConsumes.filter { !this.consumes.contains(it) }
            require(conflictingConsumes.isEmpty()) { "Conflicting consumes: $conflictingConsumes" }
        }
        val otherProduces = matcher.produces
        if (this.produces.isNotEmpty()) {
            val conflictingProduces = otherConsumes.filter { !this.produces.contains(it) }
            require(conflictingProduces.isEmpty()) { "Conflicting produces: $conflictingProduces" }
        }
        val otherPredicate = matcher.predicate
        return HttpRequestMatcher.from(
            pathPattern = if (this.pathPattern != null && otherPath != null) {
                otherPath.subPath(this.pathPattern)
            } else {
                otherPath ?: this.pathPattern
            },
            methods = otherMethods.ifEmpty { this.methods },
            consumes = otherConsumes.ifEmpty { this.consumes },
            produces = otherProduces.ifEmpty { this.produces },
            predicate = { request: HttpRequestHead ->
                this.predicate.accepts(request) &&
                    otherPredicate.accepts(request)
            },
        )
    }
}
