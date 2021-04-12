package com.ruban.kt.youtrack.crawler.data

object IssueCompilationStatistics {

    private val storage = mutableMapOf<String, MutableList<SampleCompilationType>>()

    fun registerIssue(id: String) { storage.putIfAbsent(id, mutableListOf()) }

    fun addCompilationResult(id: String, result: SampleCompilationType) { storage[id]!!.plusAssign(result) }

    val results: Map<String, List<SampleCompilationType>> get() = storage

}