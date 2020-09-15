package com.ruban.kt.youtrack.crawler.handlers

import com.ruban.kt.youtrack.crawler.*
import org.apache.log4j.Logger
import org.json.JSONObject

object SourceCodeSearcher : DataHandler() {

    override val propertyRequirements = setOf(
        PropertyField("id"),
        PropertyField("idReadable"),
        PropertyField("description"),
        PropertyField("comments", PropertyField("id")),
        PropertyField("attachments", PropertyField("id"))
    )

    override val queryRequirements = setOf(
        QueryRequest("project" to "Kotlin"),
        QueryRequest("state" to "Duplicate")
    )

    override fun invoke(data: Any): List<String>? {
        data as JSONObject
        val candidates = mutableListOf<String>()

        val id = data["id"] as String
        (data["description"] as? String)?.let { description -> candidates.addAll(findCode(description)) }

        val comments = YouTrackAccessor.requestJSONArray("/api/issues/$id/comments?fields=text")
        for (comment in comments) {
            comment as JSONObject
            (comment["text"] as? String)?.let { text -> candidates.addAll(findCode(text)) }
        }

        val attachments = YouTrackAccessor.requestJSONArray("/api/issues/$id/attachments?fields=extension,url")
        for (attachment in attachments) {
            attachment as JSONObject
            val extension = attachment["extension"] as? String ?: continue
            val url = attachment["url"] as? String ?: continue
            if (extension in interestingExtensions) {
                candidates += YouTrackAccessor.fetchData(url)
            }
        }

        return if (candidates.isNotEmpty()) candidates else null
    }

    override fun finish() = Unit

    // TODO
    private fun findCode(source: String): List<String> {
        return listOf(source)
    }

    private val interestingExtensions = setOf("txt", "kt", "kts")

}
