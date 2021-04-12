package com.ruban.kt.youtrack.crawler.data

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

object CompilerMsgCollector : MessageCollector {

    var hasCrash = false
    var hasCompilationError = false
    var crashMessages = mutableListOf<String>()
    var compilationErrorMessages = mutableListOf<String>()

    override fun clear() {
        hasCrash = false
        hasCompilationError = false
        crashMessages.clear()
        compilationErrorMessages.clear()
    }

    fun isCorrect(): Boolean {
        return !hasCrash && !hasCompilationError
    }

    override fun hasErrors(): Boolean {
        return hasCrash
    }

    override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageSourceLocation?) {
        if (severity == CompilerMessageSeverity.EXCEPTION) {
            hasCrash = true
            crashMessages.add(message)
        }
        if (severity == CompilerMessageSeverity.ERROR) {
            hasCompilationError = true
            compilationErrorMessages.add(message)
        }
    }

    // 1.3.61
//    override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation?) {
//        if (severity == CompilerMessageSeverity.EXCEPTION) {
//            hasCrash = true
//            crashMessages.add(message)
//        }
//        if (severity == CompilerMessageSeverity.ERROR) {
//            hasCompilationError = true
//            compilationErrorMessages.add(message)
//        }
//    }

}
