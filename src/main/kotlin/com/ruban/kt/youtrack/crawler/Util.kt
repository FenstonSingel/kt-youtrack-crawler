package com.ruban.kt.youtrack.crawler

operator fun MutableSet<PropertyField>.plusAssign(other: Set<PropertyField>) {
    val thisCopy = this.toSet()
    this.clear()
    (thisCopy + other).forEach { this += it }
}

operator fun Set<PropertyField>.plus(other: Set<PropertyField>): Set<PropertyField> {
    val allSubfields = this.map { it.name } + other.map { it.name }
    val result = mutableSetOf<PropertyField>()
    for (subfieldName in allSubfields) {
        val first = this.find { it.name == subfieldName }
        val second = other.find { it.name == subfieldName }
        result += when {
            first == null -> second!!
            second == null -> first
            else -> first + second
        }
    }
    return result
}
