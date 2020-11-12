package com.ruban.kt.youtrack.crawler.handlers

import com.ruban.kt.youtrack.crawler.DataHandler
import com.ruban.kt.youtrack.crawler.PropertyField
import com.ruban.kt.youtrack.crawler.QueryRequest
import com.ruban.kt.youtrack.crawler.data.SampleCandidate
import com.ruban.kt.youtrack.crawler.toJSONObjectList
import org.json.JSONArray
import org.json.JSONObject

object VersionsExtractor : DataHandler() {

    override val propertyRequirements = setOf(
        PropertyField("customFields",
            PropertyField("projectCustomField",
                PropertyField("field",
                    PropertyField("name")
                )
            ),
            PropertyField("value",
                PropertyField("name")
            )
        ),
    )

    override val queryRequirements = setOf(
        QueryRequest("project" to "Kotlin"),
        QueryRequest("state" to "Duplicate")
    )

    override fun invoke(data: Any): List<SampleCandidate<JSONObject>> {
        data as SampleCandidate<JSONObject>

        val customFields = (data.content["customFields"] as JSONArray).toJSONObjectList()
        val versionsJSON = customFields.find { obj ->
            ((obj["projectCustomField"] as JSONObject)["field"] as JSONObject)["name"] as String == "Affected versions"
        } ?: return emptyList()
        val versions = (versionsJSON["value"] as JSONArray).toJSONObjectList().map { obj -> obj["name"] as String }

        return listOf(SampleCandidate(data.content, data.id, data.group, versions, data.parent))
    }

}
