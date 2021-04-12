package com.ruban.kt.youtrack.crawler

import com.ruban.kt.youtrack.crawler.data.IssueCompilationStatistics
import com.ruban.kt.youtrack.crawler.data.SampleCandidate
import com.ruban.kt.youtrack.crawler.data.SampleCompilationType
import com.ruban.kt.youtrack.crawler.handlers.*
import org.apache.log4j.PropertyConfigurator
import org.json.JSONObject
import org.apache.log4j.Logger
import java.io.File

private val logger: Logger = Logger.getLogger("generalLogger")
private val log = { str: String -> logger.info(str) }
private val graphLinker = { candidate: SampleCandidate<*> ->
    val linkMap = mutableMapOf<String, Pair<String, String>>()
    linkMap["black"] = (candidate.group ?: "everything") to candidate.id
    if (candidate.parent != null) {
        linkMap["blue"] = candidate.parent!! to candidate.id
    }
    linkMap
}

fun getDatasetForDuplicates() {
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
        CompilingFilter(SampleCompilationType.CRASH), // SampleCandidate<String> -> SampleCandidate<String>
        DataPrinter<SampleCandidate<String>>(log),
        VersionsCounter(log),
        GraphAssembler("results", "crawler-graph", graphLinker),
        SampleExporter("results/downloaded-samples")
    )
    crawler.fetch()
}

fun getCorrectCode() {
    YouTrackAccessor.throttlingDelay = 50L
    val crawler = YouTrackCrawler(
        AllPotentialCodeCollector, // JSONObject -> SampleCandidate<JSONObject>
        DataPrinter<SampleCandidate<JSONObject>>(log),
        SourceCodeSearcher, // SampleCandidate<JSONObject> -> SampleCandidate<String>
        TextLengthFilter, // SampleCandidate<String> -> SampleCandidate<String>
        DataPrinter<SampleCandidate<String>>(log),
        CompilingFilter(SampleCompilationType.CORRECT), // SampleCandidate<String> -> SampleCandidate<String>
        DataPrinter<SampleCandidate<String>>(log),
        SampleExporter("results/downloaded-correct-samples")
    )
    crawler.fetch()
}

fun main() {
    PropertyConfigurator.configure("src/main/resources/log4j.properties")

    File("results").mkdir()

    getDatasetForDuplicates()

    val issueCompilationStatistics = IssueCompilationStatistics.results
    val issuesWithCorrectSamples = issueCompilationStatistics.filter {
            (_, samples) -> samples.any {sample -> sample == SampleCompilationType.CORRECT }
    }
    val issuesWithCrashes = issueCompilationStatistics.filter {
            (_, samples) -> samples.any {sample -> sample == SampleCompilationType.CRASH }
    }

    logger.info("Total issues fetched: ${issueCompilationStatistics.size}")
    logger.info("Issues with correct samples found: ${issuesWithCorrectSamples.size}")
    logger.info("Issues with crashes found: ${issuesWithCrashes.size}")
}
