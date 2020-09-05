package com.ruban.kt.youtrack.crawler

open class RequestDetails(
    vararg args: String
) {
    private val fields = args.toList()

    override fun toString(): String {
        val stringBuilder = StringBuilder("fields=\$type")
        fields.forEach { field -> stringBuilder.append(",$field") }
        return stringBuilder.toString()
    }
}