package com.ruban.kt.youtrack.crawler

import com.ruban.kt.youtrack.crawler.handlers.*
import org.apache.log4j.PropertyConfigurator

fun main() {
    PropertyConfigurator.configure("src/main/resources/log4j.properties")

    val crawler = YouTrackCrawler(
        SourceCodeSearcher,
        DataPrinter<List<String>>()
    )
    crawler.fetch()
}
