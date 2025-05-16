package com.coditory.kttp.server

class HttpPathPattern private constructor(
    val pattern: String,
) : Comparable<HttpPathPattern> {
    private val regex: Regex by lazy {
        val starChars = "a-zA-Z0-9-_"
        val pattern = pattern
            .replace("**", "[$starChars/]+")
            .replace("*", "[$starChars]+")
        Regex(pattern)
    }

    fun matches(path: String): Boolean {
        return regex.matches(path)
    }

    override fun compareTo(other: HttpPathPattern): Int {
        return this.pattern.compareTo(other.pattern)
    }

    fun subPath(path: String): HttpPathPattern {
        val normalized = normalizePath(this.pattern + path)
        return HttpPathPattern(normalized)
    }

    fun subPath(path: HttpPathPattern): HttpPathPattern {
        return subPath(path.pattern)
    }

    override fun toString(): String {
        return pattern
    }

    override fun equals(other: Any?): Boolean {
        if (other !is HttpPathPattern) return false
        return pattern == other.pattern
    }

    override fun hashCode(): Int {
        return pattern.hashCode()
    }

    companion object {
        private val multipleSlashes = Regex("//+")
        private val multipleStars = Regex("\\*\\*+")

        private fun normalizePath(path: String): String {
            val result = path
                .replace(multipleSlashes, "/")
                .replace(multipleStars, "**")
                .trimEnd('/')
            return if (!result.startsWith('/')) "/$result" else result
        }

        fun from(pattern: String): HttpPathPattern {
            return HttpPathPattern(normalizePath(pattern))
        }
    }
}
