package com.ruban.kt.youtrack.crawler.handlers

import com.ruban.kt.youtrack.crawler.DataHandler

class DataPrinter<T : Any>(
    private val printer: (String) -> Unit
) : DataHandler() {

    override fun invoke(data: Any): List<T> {
        data as T

        ++numberOfDataObjects
        printer(data.toString())

        return listOf(data)
    }

    override fun finish(): List<T> {
        printer("Number of data objects is $numberOfDataObjects.")
        printer("")
        return emptyList()
    }

    private var numberOfDataObjects = 0

}
