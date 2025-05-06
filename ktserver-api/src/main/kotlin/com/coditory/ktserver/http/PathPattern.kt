package com.coditory.ktserver.http

data class PathPattern(
    private val pattern: String,
) : Comparable<PathPattern> {
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

    fun prefix(path: String): PathPattern {
        val combined = (path + "/" + this.pattern)
            .replace(Regex("/+"), "/")
            .replace(Regex("/$"), "")
            .replace("/?", "?")
            .replace("/#", "#")
        return PathPattern(combined)
    }

    override fun compareTo(other: PathPattern): Int {
        return this.pattern.compareTo(other.pattern)
    }
}
