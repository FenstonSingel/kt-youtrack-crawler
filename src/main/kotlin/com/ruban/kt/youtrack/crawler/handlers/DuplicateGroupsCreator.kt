package com.ruban.kt.youtrack.crawler.handlers

import com.ruban.kt.youtrack.crawler.*
import com.ruban.kt.youtrack.crawler.data.*
import org.json.JSONArray
import org.json.JSONObject

object DuplicateGroupsCreator : DataHandler() {

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
        data as JSONObject

        val links = (data["links"] as JSONArray).toJSONObjectList()

        val duplicateLink = links.find { link ->
            (link["linkType"] as JSONObject)["name"] as String == "Duplicate" && link["direction"] as String == "INWARD"
        }!!
        val duplicatedIssueList = (duplicateLink["issues"] as JSONArray).toJSONObjectList()
        if (duplicatedIssueList.isEmpty()) return emptyList()
        val duplicatedIssueID = duplicatedIssueList[0]["id"] as String

        val duplicatedIssue = YouTrackAccessor.requestJSONObject("/api/issues/$duplicatedIssueID?${crawler.properties}")
        val duplicatedIssueIDReadable = duplicatedIssue["idReadable"] as String
        return if (duplicatedIssueIDReadable.startsWith("KT-")) {
            duplicatedIssues += JSONObjectDistinguishableByID(duplicatedIssueIDReadable, duplicatedIssue)
            IssueCompilationStatistics.registerIssue(data["idReadable"] as String)
            listOf(SampleCandidate(data, data["idReadable"] as String, duplicatedIssueIDReadable))
        } else {
            emptyList()
        }
    }

    override fun finish(): List<SampleCandidate<JSONObject>> {
        duplicatedIssues.forEach { issue -> IssueCompilationStatistics.registerIssue(issue.id) }
        return duplicatedIssues.map { obj -> SampleCandidate(obj.data, obj.id, obj.id) }
    }

    private class JSONObjectDistinguishableByID(val id: String, val data: JSONObject) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as JSONObjectDistinguishableByID

            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int {
            return id.hashCode()
        }
    }

    private val duplicatedIssues = mutableSetOf<JSONObjectDistinguishableByID>()

}
