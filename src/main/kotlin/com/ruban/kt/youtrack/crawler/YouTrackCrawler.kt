package com.ruban.kt.youtrack.crawler

import org.apache.log4j.Logger
import org.json.JSONObject

class YouTrackCrawler(private val handlers: List<DataHandler>) {

    constructor(vararg handlers: DataHandler) : this(handlers.toList())

    fun fetch(): List<Any> {
        val currData = mutableListOf<Any>()
        currData.addAll(getAllIssues())
        val newData = mutableListOf<Any>()
        for (handler in handlers) {
            logger.debug("Applying handler $handler ...")
            for (element in currData) {
                val newElement = handler(element) ?: continue
                newData += newElement
            }
            currData.clear()
            currData.addAll(newData)
            newData.clear()
            handler.finish()
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
            stringBuilder.append(if (properties.isNotEmpty()) "&fields=\$this,$properties" else "")
            stringBuilder.append(if (query.isNotEmpty()) "&query=$query" else "")
            val currIssues = YouTrackAccessor.requestJSONArray(stringBuilder.toString())
            for (i in 0 until currIssues.length()) {
                result += currIssues.getJSONObject(i)
            }
            lastNumberOfIssues = currIssues.length()
            skip += lastNumberOfIssues
        }
        logger.debug("Total number of issues fetched: $skip")
        return result
    }

    private val properties: String
    private val query: String

    private val logger: Logger = Logger.getLogger("generalLogger")

    init {
        val initProperties = mutableSetOf<PropertyField>()
        val initQuery = mutableSetOf<QueryRequest>()
        for (handler in handlers) {
            initProperties += handler.propertyRequirements
            initQuery += handler.queryRequirements
        }
        properties = initProperties.joinToString(",")
        query = initQuery.joinToString("%20")
        logger.debug("Final properties: $properties")
        logger.debug("Final query: $query")
    }

}
