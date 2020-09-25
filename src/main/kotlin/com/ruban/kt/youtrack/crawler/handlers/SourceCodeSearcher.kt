package com.ruban.kt.youtrack.crawler.handlers

import com.ruban.kt.youtrack.crawler.*
import com.ruban.kt.youtrack.crawler.data.*
import org.json.JSONArray
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

    override fun invoke(data: Any): List<SampleCandidate<String>> {
        data as SampleCandidate<JSONObject>
        val (jsonObject, groupID, versions) = data

        val candidates = mutableListOf<String>()

        val id = jsonObject["id"] as String
        (jsonObject["description"] as? String)?.let { description -> candidates.addAll(findCode(description)) }

        val source = YouTrackAccessor.requestJSONObject(
            "/api/issues/$id?fields=comments(text),attachments(extension,url)"
        )
        val comments = (source["comments"] as JSONArray).toJSONObjectList()
        for (comment in comments) {
            (comment["text"] as? String)?.let { text -> candidates.addAll(findCode(text)) }
        }
        val attachments = (source["attachments"] as JSONArray).toJSONObjectList()
        for (attachment in attachments) {
            val extension = attachment["extension"] as? String ?: continue
            val url = attachment["url"] as? String ?: continue
            if (extension in interestingExtensions) {
                candidates += YouTrackAccessor.fetchData(url)
            }
        }

        return candidates.map { candidate ->
            SampleCandidate(
                candidate,
                groupID,
                versions
            )
        }
    }

    private fun findCode(source: String): List<String> {
        val result = mutableListOf<String>()
        result.addAll(regexMarkdown.findAll(source).map { matchResult -> matchResult.groupValues[1] })
        result.addAll(regexYouTrackWiki.findAll(source).map { matchResult -> matchResult.groupValues[1] })
        return result
    }

    private val interestingExtensions = setOf("txt", "kt", "kts")

    private val regexMarkdown = Regex("""(?:```.*?\n(.*?)```)""", RegexOption.DOT_MATCHES_ALL)
    private val regexYouTrackWiki = Regex("""(?:\{code.*?}\n(.*?)\{code})""", RegexOption.DOT_MATCHES_ALL)

}
