package com.ruban.kt.youtrack.crawler.handlers

import com.ruban.kt.youtrack.crawler.DataHandler
import com.ruban.kt.youtrack.crawler.data.SampleCandidate

object TextLengthFilter : DataHandler() {

    override operator fun invoke(data: Any): List<SampleCandidate<String>> {
        data as SampleCandidate<String>

        return if (data.content.length > 1e5) emptyList() else listOf(data)
    }

}
