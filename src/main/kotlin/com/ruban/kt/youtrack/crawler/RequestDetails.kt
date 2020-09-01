package com.ruban.kt.youtrack.crawler

open class RequestDetails(
    val id: Boolean,
    val summary: Boolean
) {
    override fun toString(): String {
        val stringBuilder = StringBuilder("fields=\$type")
        if (id) stringBuilder.append(",id")
        if (summary) stringBuilder.append(",summary")
        return stringBuilder.toString()
    }
}