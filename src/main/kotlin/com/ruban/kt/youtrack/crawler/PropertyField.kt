package com.ruban.kt.youtrack.crawler

data class PropertyField(val name: String, private val subfields: Set<PropertyField>) {

    constructor(name: String, vararg subfields: PropertyField) : this(name, subfields.toSet())

    override fun toString(): String {
        val stringBuilder = StringBuilder(name)
        if (subfields.isNotEmpty()) {
            stringBuilder.append("(")
            stringBuilder.append("\$type")
            for (subfield in subfields) {
                stringBuilder.append(",$subfield")
            }
            stringBuilder.append(")")
        }
        return stringBuilder.toString()
    }

    operator fun plus(other: PropertyField): PropertyField {
        require(this.name == other.name) { "Different properties cannot be merged." }
        return PropertyField(this.name, this.subfields + other.subfields)
    }

}
