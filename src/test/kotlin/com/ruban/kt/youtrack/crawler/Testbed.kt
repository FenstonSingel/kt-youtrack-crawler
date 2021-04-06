package com.ruban.kt.youtrack.crawler

import com.ruban.kt.youtrack.crawler.data.SampleCandidate
import com.ruban.kt.youtrack.crawler.handlers.*
import org.apache.log4j.PropertyConfigurator
import org.json.JSONObject
import org.apache.log4j.Logger
import java.io.File

private val logger: Logger = Logger.getLogger("generalLogger")
private val log = { str: String -> logger.info(str) }
private val graphLinker = { candidate: SampleCandidate<*> ->
    val linkMap = mutableMapOf<String, Pair<String, String>>()
    linkMap["black"] = candidate.group to candidate.id
    if (candidate.parent != null) {
        linkMap["blue"] = candidate.parent!! to candidate.id
    }
    linkMap
}

fun main() {
    PropertyConfigurator.configure("src/main/resources/log4j.properties")

    File("results").mkdir()

    val crawler = YouTrackCrawler(
        DuplicateGroupsCreator, // JSONObject -> SampleCandidate<JSONObject>
        SubtaskLinker,
        DataPrinter<SampleCandidate<JSONObject>>(log),
        GraphAssembler("results", "crawler-graph-1", graphLinker),
        VersionsExtractor, // SampleCandidate<JSONObject> -> SampleCandidate<JSONObject>
        DataPrinter<SampleCandidate<JSONObject>>(log),
        VersionsCounter(log),
        GraphAssembler("results", "crawler-graph-2", graphLinker),
        SourceCodeSearcher, // SampleCandidate<JSONObject> -> SampleCandidate<String>
        TextLengthFilter, // SampleCandidate<String> -> SampleCandidate<String>
        DataPrinter<SampleCandidate<String>>(log),
        VersionsCounter(log),
        GraphAssembler("results", "crawler-graph-3", graphLinker),
        CompilingFilter(), // SampleCandidate<String> -> SampleCandidate<String>
        DataPrinter<SampleCandidate<String>>(log),
        VersionsCounter(log),
        GraphAssembler("results", "crawler-graph", graphLinker),
        SampleExporter("results/downloaded-samples")
    )
    crawler.fetch()
}
