package com.ruban.kt.youtrack.crawler

import org.apache.log4j.Logger
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

object YouTrackAccessor {

    fun requestJSONObject(content: String): JSONObject = JSONObject(fetchData(content))
    fun requestJSONArray(content: String): JSONArray = JSONArray(fetchData(content))

    fun fetchData(content: String): String {
        Thread.sleep(throttlingDelay)
        logger.debug("Fetching data from $content ...")
        val url = URL("$baseURL$content")
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

    var throttlingDelay = 0L

    private const val baseURL = "https://youtrack.jetbrains.com"

    private val logger: Logger = Logger.getLogger("debugLogger")

}
