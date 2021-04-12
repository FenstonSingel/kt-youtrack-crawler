package com.ruban.kt.youtrack.crawler.handlers

import com.ruban.kt.youtrack.crawler.*
import com.ruban.kt.youtrack.crawler.data.IssueCompilationStatistics
import com.ruban.kt.youtrack.crawler.data.SampleCandidate
import org.json.JSONObject

object AllPotentialCodeCollector : DataHandler() {

    override val propertyRequirements = setOf(
        PropertyField("id"),
        PropertyField("idReadable")
    )

    override val queryRequirements = setOf(
        QueryRequest("project" to "Kotlin")
    )

    override fun invoke(data: Any): List<SampleCandidate<JSONObject>> {
        data as JSONObject
        IssueCompilationStatistics.registerIssue(data["idReadable"] as String)
        return listOf(SampleCandidate(data, data["idReadable"] as String))
    }

}