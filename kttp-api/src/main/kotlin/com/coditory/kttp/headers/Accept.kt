package com.coditory.kttp.headers

import com.coditory.kttp.HttpSerializable

data class Accept(
    val mediaTypes: List<MediaType>,
) : HttpSerializable {
    val value by lazy { mediaTypes.joinToString(",") { it.value } }

    override fun toString() = toHttpString()

    override fun toHttpString(builder: Appendable) {
        builder.append(value)
    }

    fun matches(mediaType: MediaType): Boolean {
        return mediaTypes.any { it.contains(mediaType) }
    }

    fun sortedByQuality(): Accept {
        val sorted = mediaTypes.sortedBy { -1.0f * (it.quality() ?: 1.0f) }
        return Accept(sorted)
    }

    companion object {
        fun parse(values: List<String>): Accept? {
            val unsortedItems = values.flatMap { HttpHeaderValue.parse(it)?.items ?: emptyList() }
            val sortedMediaTypes = HttpHeaderValue(unsortedItems)
                .sortedByQuality()
                .items
                .mapNotNull { MediaType.parse(it) }
            return if (sortedMediaTypes.isEmpty()) null else Accept(sortedMediaTypes)
        }

        fun parse(value: String): Accept? {
            val header = HttpHeaderValue.parse(value) ?: return null
            val mediaTypes = header.items.mapNotNull { MediaType.parse(it) }
            return if (mediaTypes.isEmpty()) null else Accept(mediaTypes)
        }
    }
}
