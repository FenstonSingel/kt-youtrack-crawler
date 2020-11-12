package com.ruban.kt.youtrack.crawler.handlers

import com.ruban.kt.youtrack.crawler.*
import com.ruban.kt.youtrack.crawler.data.SampleCandidate
import org.json.JSONArray
import org.json.JSONObject

object SubtaskLinker : DataHandler() {

    override val propertyRequirements = setOf(
        PropertyField("id"),
        PropertyField("idReadable"),
        PropertyField("links",
            PropertyField("direction"),
            PropertyField("linkType",
                PropertyField("name")
            ),
            PropertyField("issues",
                PropertyField("id")
            ),
        )
    )

    override val queryRequirements = setOf(
        QueryRequest("project" to "Kotlin"),
        QueryRequest("state" to "Duplicate")
    )

    override fun invoke(data: Any): List<SampleCandidate<JSONObject>> {
        data as SampleCandidate<JSONObject>

        val (jsonObject, id, group, _, _) = data
        val links = (jsonObject["links"] as JSONArray).toJSONObjectList()

        var parentIssueIDReadable: String? = null
        val parentLink = links.find { link ->
            (link["linkType"] as JSONObject)["name"] as String == "Subtask" && link["direction"] as String == "INWARD"
        }!!
        val parentIssueList = (parentLink["issues"] as JSONArray).toJSONObjectList()
        if (parentIssueList.isNotEmpty()) {
            val parentIssueID = parentIssueList[0]["id"] as String
            val parentIssue = YouTrackAccessor.requestJSONObject("/api/issues/$parentIssueID?${crawler.properties}")
            parentIssueIDReadable = parentIssue["idReadable"] as String
        }

        return listOf(SampleCandidate(jsonObject, id, group, parent = parentIssueIDReadable))
    }

    override fun finish(): List<SampleCandidate<JSONObject>> {
        return emptyList()
    }

}