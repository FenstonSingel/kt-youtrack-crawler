package com.ruban.kt.youtrack.crawler.handlers

import com.ruban.kt.youtrack.crawler.DataHandler
import com.ruban.kt.youtrack.crawler.PropertyField
import com.ruban.kt.youtrack.crawler.QueryRequest
import org.apache.log4j.Logger
import java.io.PrintStream

class DataPrinter<T>(
    override val propertyRequirements: Set<PropertyField> = emptySet(),
    override val queryRequirements: Set<QueryRequest> = emptySet(),
    private val stream: PrintStream = System.out
) : DataHandler() {

    override fun invoke(data: Any): T? {
        data as T
        ++numberOfIssues
        stream.println(data)
        stream.println()
        return data
    }

    override fun finish() {
        stream.println("Number of data objects is $numberOfIssues.")
    }

    private var numberOfIssues = 0

}
