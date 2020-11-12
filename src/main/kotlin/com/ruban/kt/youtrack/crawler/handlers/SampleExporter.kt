package com.ruban.kt.youtrack.crawler.handlers

import com.ruban.kt.youtrack.crawler.DataHandler
import com.ruban.kt.youtrack.crawler.data.SampleCandidate
import java.io.File

class SampleExporter(
    private val dirPath: String
) : DataHandler() {

    override fun invoke(data: Any): List<SampleCandidate<String>> {
        data as SampleCandidate<String>

        val groupFile = File("$dirPath/${data.group}")
        groupFile.mkdirs()
        val sampleNumber = (groupFile.listFiles()?.size ?: 0) + 1
        val annotatedContent = "// Original bug: ${data.id}\n// Duplicated bug: ${data.group}\n\n${data.content}"
        File("$dirPath/${data.group}/$sampleNumber.kt").writeText(annotatedContent)

        return listOf(data)
    }

}
