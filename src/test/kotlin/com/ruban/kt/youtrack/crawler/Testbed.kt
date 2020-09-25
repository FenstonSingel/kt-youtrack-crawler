package com.ruban.kt.youtrack.crawler

import com.ruban.kt.youtrack.crawler.data.SampleCandidate
import com.ruban.kt.youtrack.crawler.handlers.*
import org.apache.log4j.PropertyConfigurator
import org.json.JSONObject
import org.apache.log4j.Logger

private val logger: Logger = Logger.getLogger("generalLogger")
private val log: (String) -> Unit = { str -> logger.info(str) }

fun main() {
    PropertyConfigurator.configure("src/main/resources/log4j.properties")

    val crawler = YouTrackCrawler(
        DuplicateGroupsCreator, // JSONObject -> SampleCandidate<JSONObject>
        DataPrinter<SampleCandidate<JSONObject>>(log),
        VersionsExtractor, // SampleCandidate<JSONObject> -> SampleCandidate<JSONObject>
        DataPrinter<SampleCandidate<JSONObject>>(log),
        VersionsCounter(log),
        SourceCodeSearcher, // SampleCandidate<JSONObject> -> SampleCandidate<String>
        DataPrinter<SampleCandidate<String>>(log),
        VersionsCounter(log),
        CompilingFilter(), // SampleCandidate<String> -> SampleCandidate<String>
        DataPrinter<SampleCandidate<String>>(log),
        VersionsCounter(log),
        SampleExporter("/home/ruban/kotlin-samples/ground-truth")
    )
    crawler.fetch()

//    val compiler = CompilingFilter()
//    val result = compiler.invoke(SampleCandidate("""
//        suspend inline fun f(g: suspend () -> Int): Int = g()
//
//        val condition = false
//
//        suspend fun main() {
//            if (condition) {
//                f { 1 }
//            }
//        }
//    """.trimIndent(), "1"))
//    println(result)

//    val exporter = SampleExporter("/home/ruban/kotlin-samples/ground-truth")
//    exporter.invoke(SampleCandidate("""
//        suspend inline fun f(g: suspend () -> Int): Int = g()
//
//        val condition = false
//
//        suspend fun main() {
//            if (condition) {
//                f { 1 }
//            }
//        }
//    """.trimIndent(), "KT-666"))
//    exporter.invoke(SampleCandidate("""
//        suspend fun fetchAllPages(): PaginatedVO {
//            suspend fun PaginatedVO.getNextPages(): PaginatedVO {
//                return this.let {
//                    this.getNextPages()
//                }
//            }
//            return fetchData().getNextPages()
//        }
//
//        fun fetchData(): PaginatedVO {
//            return PaginatedVO()
//        }
//
//        class PaginatedVO
//    """.trimIndent(), "KT-420"))
//    exporter.invoke(SampleCandidate("""
//        class Reproducer() {
//
//            suspend fun f(a: List<String>) {
//                suspend fun recurse(current: List<String>) {
//                    current.forEach { recurse(listOf(it)) }
//                }
//
//                recurse(a)
//            }
//        }
//    """.trimIndent(), "KT-420"))
}
