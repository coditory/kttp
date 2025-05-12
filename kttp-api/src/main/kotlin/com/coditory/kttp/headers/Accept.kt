package com.coditory.kttp.headers

data class Accept(
    val contentTypes: List<ContentType>,
) {
    fun matches(contentType: ContentType): Boolean {
        return contentTypes.any { it.matches(contentType) }
    }

    companion object {
        fun parse(values: List<String>): Accept? {
            val unsortedItems = values.flatMap { HttpHeaderValue.parse(it)?.items ?: emptyList() }
            val sortedContentTypes = HttpHeaderValue(unsortedItems).items
                .mapNotNull { ContentType.parse(it) }
            return if (sortedContentTypes.isEmpty()) null else Accept(sortedContentTypes)
        }

        fun parse(value: String): Accept? {
            val header = HttpHeaderValue.parse(value) ?: return null
            val contentTypes = header.items.mapNotNull { ContentType.parse(it) }
            return if (contentTypes.isEmpty()) null else Accept(contentTypes)
        }
    }
}
