package com.ruban.kt.youtrack.crawler

abstract class DataHandler {

    abstract val propertyRequirements: Set<PropertyField>

    abstract val queryRequirements: Set<QueryRequest>

    abstract operator fun invoke(data: Any): Any?

    abstract fun finish()

    protected companion object {
        const val conveyorError: String = "Handler conveyor was constructed improperly: types of data don't match."
    }

}
