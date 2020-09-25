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
            val linkType = link["linkType"] as JSONObject
            linkType["name"] as String == "Duplicate" && link["direction"] as String == "INWARD"
        }!!
        val issues = (duplicateLink["issues"] as JSONArray).toJSONObjectList()
        if (issues.isEmpty()) return emptyList()
        val parentIssueID = issues[0]["id"] as String

        val parentIssue = YouTrackAccessor.requestJSONObject("/api/issues/$parentIssueID?${crawler.properties}")
        val parentIDReadable = (parentIssue["idReadable"] as String)
        if (parentIDReadable.startsWith("KT-")) {
            parents += JSONObjectDistinguishableByID(parentIDReadable, parentIssue)
        }

        return listOf(SampleCandidate(data, parentIDReadable))
    }

    override fun finish(): List<SampleCandidate<JSONObject>> {
        return parents.map { obj -> SampleCandidate(obj.data, obj.id) }
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

    private val parents = mutableSetOf<JSONObjectDistinguishableByID>()

}
