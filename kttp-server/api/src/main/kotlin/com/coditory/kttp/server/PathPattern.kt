package com.coditory.kttp.server

data class PathPattern(
    val pattern: String,
) : Comparable<PathPattern> {
    private val regex: Regex by lazy {
        val starChars = "a-zA-Z0-9-_"
        val pattern = pattern
            .replace(Regex("\\*\\*+"), "**")
            .replace("**", "[$starChars/]+")
            .replace("*", "[$starChars]+")
        Regex(pattern)
    }

    fun matches(path: String): Boolean {
        return regex.matches(path)
    }

    override fun compareTo(other: PathPattern): Int {
        return this.pattern.compareTo(other.pattern)
    }
}
