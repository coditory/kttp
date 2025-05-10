package com.coditory.kttp

interface HttpSerializable {
    fun toHttpString(builder: Appendable)

    fun toHttpString(): String {
        val builder = StringBuilder()
        toHttpString(builder)
        return builder.toString()
    }
}
