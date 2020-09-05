package com.ruban.kt.youtrack.crawler

fun main() {
    val crawler = YouTrackCrawler(
        RequestDetails("idReadable"),
        query = "project:Kotlin" + "%20" + "type:Bug" + "%20" + "state:Duplicate"
    )
    crawler.test()
}