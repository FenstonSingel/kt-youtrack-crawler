package com.ruban.kt.youtrack.crawler

import com.ruban.kt.youtrack.crawler.requests.*

fun main() {
    val crawler = YouTrackCrawler(RequestAll, query = "project:%20Kotlin" + "%20" + "type:%20Bug")
    crawler.test()
}