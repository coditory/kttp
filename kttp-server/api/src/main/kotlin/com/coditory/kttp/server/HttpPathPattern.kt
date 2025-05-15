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
        val normalized = normalizePath(path)
        return HttpPathPattern(this.pattern + normalized)
    }

    fun subPath(path: HttpPathPattern): HttpPathPattern {
        return HttpPathPattern(this.pattern + path.pattern)
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
