package com.ruban.kt.youtrack.crawler.handlers

import com.ruban.kt.youtrack.crawler.DataHandler
import com.ruban.kt.youtrack.crawler.data.SampleCandidate
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class GraphAssembler<T : Any>(
    private val destinationDir: String,
    private val destinationFilePath: String,
    private val linker: (T) -> Map<String, Pair<String, String>>
) : DataHandler() {

    override fun invoke(data: Any): List<T> {
        data as T

        val linkMap = linker(data)
        linkMap.forEach { (color, link) ->
            links.getOrPut(color) { mutableSetOf() } += link
        }

        return listOf(data)
    }

    override fun finish(): List<SampleCandidate<JSONObject>> {
        val stringRepresentation = links
            .map { (color, linkList) ->
                val linksStr = linkList.joinToString(separator = "") { (from, to) ->
                    "\n\t${if (from == to) "\"$from\"" else "\"$from\" -> \"$to\""}"
                }
                "\n\tedge [color=$color];$linksStr"
            }
            .joinToString(separator = "")
        File("$destinationDir/$destinationFilePath.dot").writeText("digraph Data {$stringRepresentation\n}")
        return emptyList()
    }

    private val links = mutableMapOf<String, MutableSet<Pair<String, String>>>()

}
