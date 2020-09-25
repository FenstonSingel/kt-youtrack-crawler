package com.ruban.kt.youtrack.crawler

import org.json.JSONArray
import org.json.JSONObject

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

fun JSONArray.toJSONObjectList(): List<JSONObject> {
    val result = mutableListOf<JSONObject>()
    for (i in 0 until length()) {
        result += getJSONObject(i)
    }
    return result
}
