package com.coditory.kttp

interface HttpParams {
    operator fun plus(other: HttpParams): HttpParams

    operator fun minus(other: HttpParams): HttpParams

    fun with(
        other: HttpParams,
        overrideEntries: Boolean = false,
        allowDuplicates: Boolean = false,
    ): HttpParams

    fun with(
        name: String,
        value: String,
        overrideEntry: Boolean = false,
        allowDuplicates: Boolean = false,
    ): HttpParams

    fun with(
        name: String,
        values: List<String>,
        overrideEntry: Boolean = false,
        allowDuplicates: Boolean = false,
    ): HttpParams

    fun withMap(
        other: Map<String, String>,
        overrideEntries: Boolean = false,
        allowDuplicates: Boolean = false,
    ): HttpParams

    fun withMultiMap(
        other: Map<String, List<String>>,
        overrideEntries: Boolean = false,
        allowDuplicates: Boolean = false,
    ): HttpParams

    fun without(other: HttpParams): HttpParams

    fun without(vararg pairs: Pair<String, String>): HttpParams

    fun without(name: String, value: String): HttpParams

    fun withoutMap(other: Map<String, String>): HttpParams

    fun withoutMultiMap(other: Map<String, List<String>>): HttpParams

    fun without(name: String): HttpParams

    fun without(names: Collection<String>): HttpParams

    fun toMap(): Map<String, List<String>>

    operator fun get(name: String): String? = getAll(name)?.firstOrNull()

    fun getAll(name: String): List<String>?

    operator fun contains(name: String): Boolean = getAll(name) != null

    fun contains(name: String, value: String): Boolean = contains(name, listOf(value))

    fun contains(name: String, values: List<String>): Boolean

    fun containsAll(other: HttpParams): Boolean {
        return other.toMap().entries.all { entry ->
            this.contains(entry.key, entry.value)
        }
    }

    fun containsValue(value: String): Boolean

    fun names(): Set<String> = toMap().keys

    fun isEmpty(): Boolean = toMap().isEmpty()

    fun entries(): Set<Map.Entry<String, List<String>>> = toMap().entries

    fun forEach(body: (String, List<String>) -> Unit) {
        for ((key, value) in toMap()) body(key, value)
    }

    fun forEachEntry(body: (String, String) -> Unit) {
        for ((key, value) in toMap()) {
            value.forEach { body(key, it) }
        }
    }

    companion object {
        private val EMPTY = MutableMapHttpParams()
        fun empty(): HttpParams = EMPTY
        fun from(vararg values: Pair<String, String>): HttpParams = fromMap(values.toMap())
        fun fromMap(values: Map<String, String>): HttpParams = MutableHttpParams.fromMap(values)
        fun fromMultiMap(values: Map<String, List<String>>): HttpParams = MutableHttpParams.fromMultiMap(values)
    }
}

interface MutableHttpParams : HttpParams {
    override operator fun plus(other: HttpParams): MutableHttpParams

    override operator fun minus(other: HttpParams): MutableHttpParams

    override fun with(
        other: HttpParams,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): MutableHttpParams

    override fun with(
        name: String,
        value: String,
        overrideEntry: Boolean,
        allowDuplicates: Boolean,
    ): MutableHttpParams

    override fun with(
        name: String,
        values: List<String>,
        overrideEntry: Boolean,
        allowDuplicates: Boolean,
    ): MutableHttpParams

    override fun withMap(
        other: Map<String, String>,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): MutableHttpParams

    override fun withMultiMap(
        other: Map<String, List<String>>,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): MutableHttpParams

    override fun without(other: HttpParams): MutableHttpParams

    override fun without(vararg pairs: Pair<String, String>): MutableHttpParams

    override fun without(name: String, value: String): MutableHttpParams

    override fun withoutMap(other: Map<String, String>): MutableHttpParams

    override fun withoutMultiMap(other: Map<String, List<String>>): MutableHttpParams

    override fun without(name: String): MutableHttpParams

    override fun without(names: Collection<String>): MutableHttpParams

    fun add(
        other: HttpParams,
        allowDuplicates: Boolean = false,
    ) = addMultiMap(other.toMap(), allowDuplicates)

    fun add(
        name: String,
        value: String,
        allowDuplicates: Boolean = false,
    ) = addMultiMap(mapOf(name to listOf(value)), allowDuplicates)

    fun add(
        name: String,
        values: List<String>,
        allowDuplicates: Boolean = false,
    ) = addMultiMap(mapOf(name to values), allowDuplicates)

    fun addMap(
        other: Map<String, String>,
        allowDuplicates: Boolean = false,
    ) = addMultiMap(toMultiMap(other), allowDuplicates)

    fun addMultiMap(
        other: Map<String, List<String>>,
        allowDuplicates: Boolean = false,
    ): Boolean

    fun set(other: HttpParams) = setMultiMap(other.toMap())

    fun set(name: String, value: String?) = if (value == null) {
        setMultiMap(mapOf(name to emptyList()))
    } else {
        setMultiMap(mapOf(name to listOf(value)))
    }

    fun set(name: String, values: List<String>) = setMultiMap(mapOf(name to values))

    fun setMap(other: Map<String, String>) = setMultiMap(toMultiMap(other))

    fun setMultiMap(other: Map<String, List<String>>): Boolean

    fun remove(name: String): Boolean = remove(setOf(name))

    fun remove(names: Collection<String>): Boolean

    fun remove(other: HttpParams): Boolean = removeMultiMap(other.toMap())

    fun remove(name: String, value: String) = removeMultiMap(mapOf(name to listOf(value)))

    fun remove(name: String, values: List<String>) = removeMultiMap(mapOf(name to values))

    fun removeMap(other: Map<String, String>) = removeMultiMap(toMultiMap(other))

    fun removeMultiMap(other: Map<String, List<String>>): Boolean

    companion object {
        fun empty(): MutableHttpParams = MutableMapHttpParams()
        fun from(vararg values: Pair<String, String>): MutableHttpParams = fromMap(values.toMap())
        fun fromMap(values: Map<String, String>): MutableHttpParams = empty().withMap(values)
        fun fromMultiMap(values: Map<String, List<String>>): MutableHttpParams = empty().withMultiMap(values)
    }
}

private class MutableMapHttpParams private constructor(
    private val values: MutableMap<String, List<String>> = mutableMapOf(),
    private val lowercaseNames: Boolean = true,
    private val ignoreValueCase: Boolean = false,
    private val lowercaseValues: Boolean = false,
) : MutableHttpParams {
    constructor(
        lowercaseNames: Boolean = true,
        ignoreValueCase: Boolean = false,
        lowercaseValues: Boolean = false,
    ) : this(
        values = mutableMapOf(),
        lowercaseNames = lowercaseNames,
        ignoreValueCase = ignoreValueCase,
        lowercaseValues = lowercaseValues,
    )

    private fun copy(
        values: MutableMap<String, List<String>> = this.values,
        lowercaseNames: Boolean = this.lowercaseNames,
        ignoreValueCase: Boolean = this.ignoreValueCase,
        lowercaseValues: Boolean = this.lowercaseValues,
    ): MutableMapHttpParams {
        return MutableMapHttpParams(
            values = values,
            lowercaseNames = lowercaseNames,
            ignoreValueCase = ignoreValueCase,
            lowercaseValues = lowercaseValues,
        )
    }

    override fun toMap() = values

    override fun getAll(name: String): List<String>? {
        return values[normalizeName(name)]
    }

    override fun contains(name: String): Boolean {
        return getAll(name) != null
    }

    override fun contains(name: String, values: List<String>): Boolean {
        val found = getAll(name) ?: return false
        if (values.isEmpty()) return true
        return values.all { value ->
            found.any {
                it.equals(value, ignoreCase = ignoreValueCase || lowercaseValues)
            }
        }
    }

    override fun containsValue(value: String): Boolean {
        return this.values.any { entry ->
            entry.value.any {
                it.equals(value, ignoreCase = lowercaseValues || ignoreValueCase)
            }
        }
    }

    override operator fun plus(other: HttpParams): MutableMapHttpParams = with(other)

    override operator fun minus(other: HttpParams): MutableMapHttpParams = without(other)

    override fun with(
        other: HttpParams,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ) = withMultiMap(other.toMap(), overrideEntries, allowDuplicates)

    override fun with(
        name: String,
        value: String,
        overrideEntry: Boolean,
        allowDuplicates: Boolean,
    ) = withMultiMap(mapOf(name to listOf(value)), overrideEntry, allowDuplicates)

    override fun with(
        name: String,
        values: List<String>,
        overrideEntry: Boolean,
        allowDuplicates: Boolean,
    ) = withMultiMap(mapOf(name to values), overrideEntry, allowDuplicates)

    override fun withMap(
        other: Map<String, String>,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ) = withMultiMap(toMultiMap(other), overrideEntries, allowDuplicates)

    override fun withMultiMap(
        other: Map<String, List<String>>,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): MutableMapHttpParams {
        if (other.isEmpty()) return this
        val merged = mutableMapOf<String, List<String>>()
        merged.putAll(this.values)
        val copy = copy(merged)
        val added = if (overrideEntries) {
            copy.setMultiMap(other)
        } else {
            copy.addMultiMap(other, allowDuplicates)
        }
        return if (added) copy else this
    }

    override fun add(
        other: HttpParams,
        allowDuplicates: Boolean,
    ) = addMultiMap(other.toMap(), allowDuplicates)

    override fun add(
        name: String,
        value: String,
        allowDuplicates: Boolean,
    ) = addMultiMap(mapOf(name to listOf(value)), allowDuplicates)

    override fun add(
        name: String,
        values: List<String>,
        allowDuplicates: Boolean,
    ) = addMultiMap(mapOf(name to values), allowDuplicates)

    override fun addMap(
        other: Map<String, String>,
        allowDuplicates: Boolean,
    ) = addMultiMap(toMultiMap(other), allowDuplicates)

    override fun addMultiMap(
        other: Map<String, List<String>>,
        allowDuplicates: Boolean,
    ): Boolean {
        if (other.isEmpty()) return false
        val merged = this.values
        var added = false
        other.forEach { key, values -> added = addEntry(merged, key, values, allowDuplicates) || added }
        return added
    }

    private fun addEntry(
        map: MutableMap<String, List<String>>,
        name: String,
        values: List<String>,
        allowDuplicates: Boolean,
    ): Boolean {
        if (values.isEmpty()) return false
        val prevValues = getAll(name)
        val normalizedValues = normalizeValues(values)
        if (prevValues == null) {
            map[name] = normalizedValues
        } else if (!allowDuplicates) {
            val filteredValues = normalizedValues.filter { value ->
                prevValues.none { it.equals(value, ignoreCase = ignoreValueCase) }
            }
            if (filteredValues.isEmpty()) {
                return false
            }
            map[name] = values + filteredValues
        } else {
            map[name] = values + normalizedValues
        }
        return true
    }

    override fun set(other: HttpParams) = setMultiMap(other.toMap())

    override fun set(name: String, value: String?) = if (value == null) {
        setMultiMap(mapOf(name to emptyList()))
    } else {
        setMultiMap(mapOf(name to listOf(value)))
    }

    override fun set(name: String, values: List<String>) = setMultiMap(mapOf(name to values))

    override fun setMap(other: Map<String, String>) = setMultiMap(toMultiMap(other))

    override fun setMultiMap(other: Map<String, List<String>>): Boolean {
        other.forEach { name, values ->
            if (values.isEmpty()) {
                this.values.remove(normalizeName(name))
            } else {
                this.values.put(normalizeName(name), normalizeValues(values))
            }
        }
        return true
    }

    override fun without(other: HttpParams) = withMultiMap(other.toMap())

    override fun without(vararg pairs: Pair<String, String>) = withoutMultiMap(pairs.groupBy({ it.first }, { it.second }))

    override fun without(name: String, value: String) = withoutMultiMap(mapOf(name to listOf(value)))

    override fun withoutMap(other: Map<String, String>) = withoutMultiMap(toMultiMap(other))

    override fun withoutMultiMap(other: Map<String, List<String>>): MutableMapHttpParams {
        if (other.isEmpty()) return this
        val merged = mutableMapOf<String, List<String>>()
        merged.putAll(values)
        val copy = copy(merged)
        val removed = copy.removeMultiMap(other)
        return if (removed) copy else this
    }

    override fun without(names: Collection<String>): MutableMapHttpParams {
        if (names.isEmpty()) return this
        val merged = mutableMapOf<String, List<String>>()
        merged.putAll(values)
        var removed = false
        names
            .map(this::normalizeName)
            .forEach { removed = merged.remove(it) != null || removed }
        return if (removed) copy(merged) else this
    }

    override fun without(name: String): MutableMapHttpParams {
        val normalizedName = normalizeName(name)
        if (!values.contains(normalizedName)) return this
        val merged = mutableMapOf<String, List<String>>()
        merged.putAll(values)
        merged.remove(normalizedName)
        return copy(merged)
    }

    override fun remove(name: String): Boolean {
        val normalizedName = normalizeName(name)
        return this.values.remove(normalizedName) != null
    }

    override fun remove(names: Collection<String>): Boolean {
        var removed = false
        names.forEach { removed = remove(it) || removed }
        return removed
    }

    override fun remove(other: HttpParams): Boolean = removeMultiMap(other.toMap())

    override fun remove(name: String, value: String) = removeMultiMap(mapOf(name to listOf(value)))

    override fun remove(name: String, values: List<String>) = removeMultiMap(mapOf(name to values))

    override fun removeMap(other: Map<String, String>) = removeMultiMap(toMultiMap(other))

    override fun removeMultiMap(other: Map<String, List<String>>): Boolean {
        if (other.isEmpty()) return false
        val merged = this.values
        var removed = false
        other.forEach { key, values -> removed = removeEntry(merged, key, values) || removed }
        return removed
    }

    private fun removeEntry(map: MutableMap<String, List<String>>, name: String, values: List<String>): Boolean {
        if (values.isEmpty()) return false
        val normalizedName = normalizeName(name)
        val prevValues = this.values[normalizedName] ?: return false
        val filtered = values.filter { value ->
            prevValues.none { it.equals(value, ignoreCase = ignoreValueCase || lowercaseValues) }
        }
        if (filtered.isEmpty()) {
            map.remove(normalizedName)
        } else {
            map[name] = filtered
        }
        return true
    }

    private fun normalizeName(name: String): String {
        return if (lowercaseNames) {
            name.lowercase()
        } else {
            name
        }
    }

    private fun normalizeValues(values: List<String>): List<String> {
        return values.map(this::normalizeValue)
    }

    private fun normalizeValue(value: String): String {
        return if (lowercaseValues) {
            value.lowercase()
        } else {
            value
        }
    }

    override fun toString(): String = values.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MutableHttpParams) return false
        return values == other.toMap()
    }

    override fun hashCode(): Int {
        return values.hashCode()
    }
}

internal fun toMultiMap(other: Map<String, String>): Map<String, List<String>> {
    return other
        .map { it.key to listOf(it.value) }
        .toMap()
}
