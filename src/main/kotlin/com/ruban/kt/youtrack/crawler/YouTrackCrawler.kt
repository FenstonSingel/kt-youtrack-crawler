package com.ruban.kt.youtrack.crawler

import java.net.URL
import org.json.JSONArray
import org.json.JSONObject

class YouTrackCrawler(
    private val requestDetails: RequestDetails = RequestDetails(),
    private val query: String? = null,
    private val handlers: List<IssueHandler> = emptyList()
) {

    private val baseURL = "https://youtrack.jetbrains.com/api"

    private fun sendRequest(content: String): String {
        val url = URL(content)
        val connection = url.openConnection()
        connection.connect()

        val stringBuilder = StringBuilder()
        connection.getInputStream().use { inputStream ->
            var buffer = inputStream.read()
            while (buffer != -1) {
                stringBuilder.append(buffer.toChar())
                buffer = inputStream.read()
            }
        }
        return stringBuilder.toString()
    }

    private fun getJSONObject(requestContent: String): JSONObject = JSONObject(sendRequest(requestContent))
    private fun getJSONArray(requestContent: String): JSONArray = JSONArray(sendRequest(requestContent))

    private fun getIssues(top: Int, skip: Int): JSONArray {
        val stringBuilder = StringBuilder("$baseURL/issues?")
        stringBuilder.append("\$top=$top")
        stringBuilder.append("&\$skip=$skip")
        stringBuilder.append("&$requestDetails")
        stringBuilder.append(if (query != null) "&query=$query" else "")
        return getJSONArray(stringBuilder.toString())
    }

    fun test() {
        println(requestDetails)
        val issues = getIssues(top = 1, skip = 0)
        println(issues)
        val top = 10000
        var skip = 0
        var lastNumberOfIssues = top
        while (lastNumberOfIssues == top) {
            val jsonArray = getIssues(top, skip)
            lastNumberOfIssues = jsonArray.length()
            skip += lastNumberOfIssues
            println(skip)
        }
    }

}