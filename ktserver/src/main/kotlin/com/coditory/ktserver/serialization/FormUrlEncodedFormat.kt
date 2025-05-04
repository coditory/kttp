package com.coditory.ktserver.serialization

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import java.net.URLDecoder
import java.net.URLEncoder

class FormUrlEncodedFormat(
    private val encodeDefaults: Boolean = true,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : SerialFormat {
    fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
        val output = StringBuilder()
        val encoder = FormUrlEncoder(serializersModule, output)
        encoder.encodeSerializableValue(serializer, value)
        return output.toString()
    }

    fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        val map =
            string
                .split("&")
                .map { it.split("=", limit = 2) }
                .filter { it.size == 2 }
                .associate { (key, value) ->
                    URLDecoder.decode(key, "UTF-8") to URLDecoder.decode(value, "UTF-8")
                }
        val decoder = FormUrlDecoder(serializersModule, map)
        return decoder.decodeSerializableValue(deserializer)
    }

    private inner class FormUrlEncoder(
        override val serializersModule: SerializersModule,
        private val output: StringBuilder,
    ) : Encoder, CompositeEncoder {

        private var isFirst = true
        override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
            return this
        }

        override fun endStructure(descriptor: SerialDescriptor) {
            // No action needed
        }

        private fun appendKeyValue(key: String, value: String) {
            if (!isFirst) output.append("&") else isFirst = false
            output.append(URLEncoder.encode(key, "UTF-8"))
            output.append("=")
            output.append(URLEncoder.encode(value, "UTF-8"))
        }

        override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
            val key = descriptor.getElementName(index)
            appendKeyValue(key, value)
        }

        override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
            val key = descriptor.getElementName(index)
            appendKeyValue(key, value.toString())
        }

        override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
            val key = descriptor.getElementName(index)
            appendKeyValue(key, value.toString())
        }

        override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
            val key = descriptor.getElementName(index)
            appendKeyValue(key, value.toString())
        }

        override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
            val key = descriptor.getElementName(index)
            appendKeyValue(key, value.toString())
        }

        override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
            val key = descriptor.getElementName(index)
            appendKeyValue(key, value.toString())
        }

        @ExperimentalSerializationApi
        override fun <T : Any> encodeNullableSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            serializer: SerializationStrategy<T>,
            value: T?,
        ) {
            val key = descriptor.getElementName(index)
            if (value != null) {
                encodeSerializableElement(descriptor, index, serializer, value)
            } else if (encodeDefaults) {
                appendKeyValue(key, "")
            }
        }

        override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
            val key = descriptor.getElementName(index)
            appendKeyValue(key, value.toString())
        }

        override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
            val key = descriptor.getElementName(index)
            appendKeyValue(key, value.toString())
        }

        override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
            val key = descriptor.getElementName(index)
            appendKeyValue(key, value.toString())
        }

        override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder {
            return this
        }

        override fun <T> encodeSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            serializer: SerializationStrategy<T>,
            value: T,
        ) {
            if (serializer.descriptor.kind is PrimitiveKind ||
                serializer.descriptor.kind is SerialKind.ENUM
            ) {
                serializer.serialize(this, value)
            } else {
                throw SerializationException(
                    "Unsupported type for key: ${descriptor.getElementName(index)}",
                )
            }
        }

        // Methods not needed for this format
        override fun encodeNotNullMark() {}
        override fun encodeNull() {}

        // Primitive encodings are not used directly
        override fun encodeBoolean(value: Boolean) = unsupported()
        override fun encodeByte(value: Byte) = unsupported()
        override fun encodeChar(value: Char) = unsupported()
        override fun encodeDouble(value: Double) = unsupported()
        override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) = unsupported()
        override fun encodeFloat(value: Float) = unsupported()
        override fun encodeInline(descriptor: SerialDescriptor): Encoder {
            TODO("Not yet implemented")
        }

        override fun encodeInt(value: Int) = unsupported()
        override fun encodeLong(value: Long) = unsupported()
        override fun encodeShort(value: Short) = unsupported()
        override fun encodeString(value: String) = unsupported()
        private fun unsupported(): Nothing = throw SerializationException("Primitive encoding is not supported")
    }

    private inner class FormUrlDecoder(
        override val serializersModule: SerializersModule,
        private val map: Map<String, String>,
    ) : Decoder, CompositeDecoder {

        private var currentIndex = -1
        private lateinit var descriptor: SerialDescriptor
        private val readIndices = mutableSetOf<Int>()
        override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
            this.descriptor = descriptor
            return this
        }

        override fun decodeInline(descriptor: SerialDescriptor): Decoder {
            return this
        }

        override fun endStructure(descriptor: SerialDescriptor) {
            // No action needed
        }

        override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
            for (i in 0 until descriptor.elementsCount) {
                if (i !in readIndices && map.containsKey(descriptor.getElementName(i))) {
                    readIndices.add(i)
                    return i
                }
            }
            return CompositeDecoder.DECODE_DONE
        }

        private fun getCurrentKey(index: Int): String = descriptor.getElementName(index)
        override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String {
            val key = getCurrentKey(index)
            return map[key] ?: ""
        }

        override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int {
            val key = getCurrentKey(index)
            return map[key]?.toIntOrNull() ?: 0
        }

        override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean {
            val key = getCurrentKey(index)
            return map[key]?.toBoolean() ?: false
        }

        override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte {
            val key = getCurrentKey(index)
            return map[key]?.toByteOrNull() ?: 0
        }

        override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short {
            val key = getCurrentKey(index)
            return map[key]?.toShortOrNull() ?: 0
        }

        override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long {
            val key = getCurrentKey(index)
            return map[key]?.toLongOrNull() ?: 0L
        }

        override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float {
            val key = getCurrentKey(index)
            return map[key]?.toFloatOrNull() ?: 0f
        }

        override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double {
            val key = getCurrentKey(index)
            return map[key]?.toDoubleOrNull() ?: 0.0
        }

        override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char {
            val key = getCurrentKey(index)
            return map[key]?.firstOrNull() ?: '\u0000'
        }

        override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder {
            return this
        }

        override fun <T> decodeSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            deserializer: DeserializationStrategy<T>,
            previousValue: T?,
        ): T {
            return if (deserializer.descriptor.kind is PrimitiveKind ||
                deserializer.descriptor.kind is SerialKind.ENUM
            ) {
                deserializer.deserialize(this)
            } else {
                throw SerializationException(
                    "Unsupported type for key: ${descriptor.getElementName(index)}",
                )
            }
        }

        override fun <T : Any> decodeNullableSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            deserializer: DeserializationStrategy<T?>,
            previousValue: T?,
        ): T? {
            val key = getCurrentKey(index)
            return if (map.containsKey(key)) {
                decodeSerializableElement(descriptor, index, deserializer, previousValue)
            } else {
                null
            }
        }

        // Methods not needed for this format
        override fun decodeNotNullMark(): Boolean = true
        override fun decodeNull(): Nothing? = null

        // Primitive decodings
        override fun decodeBoolean(): Boolean = unsupported()
        override fun decodeByte(): Byte = unsupported()
        override fun decodeChar(): Char = unsupported()
        override fun decodeDouble(): Double = unsupported()
        override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
            val key = getCurrentKey(currentIndex)
            val value = map[key]
            return enumDescriptor.getElementIndex(value ?: "")
        }

        override fun decodeFloat(): Float = unsupported()
        override fun decodeInt(): Int = unsupported()
        override fun decodeLong(): Long = unsupported()
        override fun decodeShort(): Short = unsupported()
        override fun decodeString(): String = unsupported()
        private fun unsupported(): Nothing = throw SerializationException("Primitive decoding is not supported")
    }

    companion object {
        private val DEFAULT = FormUrlEncodedFormat()

        fun default() = DEFAULT
    }
}
