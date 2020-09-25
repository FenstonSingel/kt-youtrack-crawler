package com.ruban.kt.youtrack.crawler.handlers

import com.ruban.kt.youtrack.crawler.DataHandler
import com.ruban.kt.youtrack.crawler.data.SampleCandidate

class VersionsCounter(
    private val printer: (String) -> Unit
) : DataHandler() {

    override fun invoke(data: Any): List<SampleCandidate<*>> {
        data as SampleCandidate<*>

        data.versions.forEach { version ->
            statistics.merge(version, 1) { old, one -> old + one }
        }

        return listOf(data)
    }

    override fun finish(): List<SampleCandidate<*>> {
        for ((version, number) in statistics) {
            printer("$version: $number samples")
        }
        printer("")
        return emptyList()
    }

    private val statistics = mutableMapOf<String, Int>()

}