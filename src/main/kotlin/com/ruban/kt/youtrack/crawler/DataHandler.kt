package com.ruban.kt.youtrack.crawler

abstract class DataHandler {

    companion object {
        fun link(crawler: YouTrackCrawler, handler: DataHandler) {
            handler.crawler = crawler
        }
    }

    open val propertyRequirements: Set<PropertyField> = emptySet()

    open val queryRequirements: Set<QueryRequest> = emptySet()

    open operator fun invoke(data: Any): Iterable<Any> = listOf(data)

    open fun finish(): Iterable<Any> = emptyList()

    protected lateinit var crawler: YouTrackCrawler

}
