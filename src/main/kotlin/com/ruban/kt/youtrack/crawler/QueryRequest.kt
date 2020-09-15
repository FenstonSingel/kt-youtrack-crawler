package com.ruban.kt.youtrack.crawler

data class QueryRequest(private val field: String, private val value: String) {

    constructor(content: Pair<String, String>) : this(content.first, content.second)

    override fun toString(): String = "$field:$value"

}
