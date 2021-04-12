package com.ruban.kt.youtrack.crawler.data

data class SampleCandidate<T>(
    val content: T,
    val id: String,
    val group: String? = null,
    val versions: List<String> = emptyList(),
    val parent: String? = null
)
