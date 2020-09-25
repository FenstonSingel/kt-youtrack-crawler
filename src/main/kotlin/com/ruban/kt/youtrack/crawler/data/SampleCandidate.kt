package com.ruban.kt.youtrack.crawler.data

data class SampleCandidate<T>(val content: T, val group: String, val versions: List<String> = emptyList())
