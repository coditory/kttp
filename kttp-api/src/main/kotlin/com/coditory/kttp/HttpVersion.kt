package com.coditory.kttp

data class HttpVersion(
    val name: String,
    val major: Int,
    val minor: Int,
) : HttpSerializable {
    val value = "$name/$major.$minor"

    override fun toString() = value
    override fun toHttpString(builder: Appendable) {
        builder.append(value)
    }

    companion object {
        val HTTP_2_0: HttpVersion = HttpVersion("HTTP", 2, 0)
        val HTTP_1_1: HttpVersion = HttpVersion("HTTP", 1, 1)
        val HTTP_1_0: HttpVersion = HttpVersion("HTTP", 1, 0)
        val SPDY_3: HttpVersion = HttpVersion("SPDY", 3, 0)
        val QUIC: HttpVersion = HttpVersion("QUIC", 1, 0)

        fun from(name: String, major: Int, minor: Int): HttpVersion = when {
            name == "HTTP" && major == 1 && minor == 0 -> HTTP_1_0
            name == "HTTP" && major == 1 && minor == 1 -> HTTP_1_1
            name == "HTTP" && major == 2 && minor == 0 -> HTTP_2_0
            else -> HttpVersion(name, major, minor)
        }

        /**
         * Format: protocol/major.minor
         */
        fun from(value: CharSequence): HttpVersion {
            val (protocol, major, minor) = value.split("/", ".").also {
                require(it.size == 3) {
                    "Failed to parse HttpProtocolVersion. Expected format: protocol/major.minor, but actual: $value"
                }
            }
            return from(protocol, major.toInt(), minor.toInt())
        }

        /**
         * Format: protocol/major.minor
         */
        fun fromOrNull(value: CharSequence): HttpVersion? {
            val (protocol, major, minor) = value.split("/", ".").also {
                if (it.size != 3) return null
            }
            return from(protocol, major.toInt(), minor.toInt())
        }
    }
}
