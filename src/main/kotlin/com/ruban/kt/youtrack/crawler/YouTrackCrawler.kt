package com.ruban.kt.youtrack.crawler

import org.apache.log4j.Logger
import org.json.JSONObject

class YouTrackCrawler
private constructor(private val handlers: List<DataHandler>) {

    constructor(vararg handlers: DataHandler) : this(handlers.toList())

    val properties: String

    fun fetch(): List<Any> {
        val currData = mutableListOf<Any>()
        currData.addAll(getAllIssues())
        val newData = mutableListOf<Any>()
        for (handler in handlers) {
            logger.debug("Applying handler $handler ...")
            for (element in currData) {
                newData.addAll(handler(element))
            }
            currData.clear()
            currData.addAll(newData)
            newData.clear()
            currData.addAll(handler.finish())
        }
        return currData
    }

    private fun getAllIssues(): List<JSONObject> {
        val result = mutableListOf<JSONObject>()
        val top = 10000
        var skip = 0
        var lastNumberOfIssues = top
        logger.debug("Starting to fetch issues from YouTrack ...")
        while (lastNumberOfIssues == top) {
            val stringBuilder = StringBuilder("/api/issues?")
            stringBuilder.append("\$top=$top")
            stringBuilder.append("&\$skip=$skip")
            if (properties.isNotEmpty()) stringBuilder.append("&$properties")
            if (query.isNotEmpty()) stringBuilder.append("&$query")
            val currIssues = YouTrackAccessor.requestJSONArray(stringBuilder.toString())
            result += currIssues.toJSONObjectList()
            lastNumberOfIssues = currIssues.length()
            skip += lastNumberOfIssues
        }
        logger.debug("Total number of issues fetched: $skip")
        return result
    }

    private val query: String

    private val logger: Logger = Logger.getLogger("debugLogger")

    init {
        val initProperties = mutableSetOf<PropertyField>()
        val initQuery = mutableSetOf<QueryRequest>()
        for (handler in handlers) {
            DataHandler.link(this, handler)
            initProperties += handler.propertyRequirements
            initQuery += handler.queryRequirements
        }
        properties = "fields=\$this${if (initProperties.isNotEmpty()) ",${initProperties.joinToString(",")}" else ""}"
        query = if (initQuery.isNotEmpty()) "query=${initQuery.joinToString("%20")}" else ""
        logger.debug("Final properties: $properties")
        logger.debug("Final query: $query")
    }

}
