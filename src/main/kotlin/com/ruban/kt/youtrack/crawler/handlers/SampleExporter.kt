package com.ruban.kt.youtrack.crawler.handlers

import com.ruban.kt.youtrack.crawler.DataHandler
import com.ruban.kt.youtrack.crawler.data.SampleCandidate
import java.io.File

class SampleExporter(
    private val dirPath: String
) : DataHandler() {

    override fun invoke(data: Any): List<SampleCandidate<String>> {
        data as SampleCandidate<String>

        val groupFilePath = if (data.group != null) "$dirPath/${data.group}" else dirPath
        val groupFile = File(groupFilePath)
        groupFile.mkdirs()
        val sampleNumber = (groupFile.listFiles()?.size ?: 0) + 1
        val annotatedContent = with(StringBuilder()) {
            append("// Original bug: ${data.id}\n")
            if (data.group != null) append("// Duplicated bug: ${data.group}\n")
            append("\n${data.content}")
        }.toString()
        File("$groupFilePath/$sampleNumber.kt").writeText(annotatedContent)

        return listOf(data)
    }

}
