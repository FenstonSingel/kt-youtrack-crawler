package com.ruban.kt.youtrack.crawler.handlers

import com.ruban.kt.youtrack.crawler.DataHandler
import com.ruban.kt.youtrack.crawler.data.CompilerMsgCollector
import com.ruban.kt.youtrack.crawler.data.SampleCandidate
import org.apache.commons.io.FileUtils
import org.apache.log4j.Logger
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.IncrementalCompilation
import org.jetbrains.kotlin.config.Services
import java.io.File

class CompilingFilter(compilerArgs: String = "") : DataHandler() {

    override operator fun invoke(data: Any): List<SampleCandidate<String>> {
        data as SampleCandidate<String>

        File(codeFile).writeText(data.content)
        if (File(buildFilesDir).exists()) FileUtils.cleanDirectory(File(buildFilesDir))
        CompilerMsgCollector.clear()

        logger.debug("Compiling the following:")
        logger.debug(data.content)
        compiler.exec(CompilerMsgCollector, Services.EMPTY, arguments)
        val result = when {
            CompilerMsgCollector.hasCrash -> "crash!"
            CompilerMsgCollector.hasCompilationError -> "incorrect code"
            else -> "correct code"
        }
        logger.debug("Result: $result")

        return if (CompilerMsgCollector.hasErrors()) listOf(data) else emptyList()
    }

    private val compiler = K2JVMCompiler()
    private val arguments: K2JVMCompilerArguments

    init {
        val argsString = "$codeFile $compilerArgs -d $buildFilesDir"
        arguments = K2JVMCompilerArguments().apply {
            K2JVMCompiler().parseArguments(
                argsString.replace(Regex(" +"), " ").split(" ").toTypedArray(),
                this
            )
        }
        arguments.jdkHome = System.getenv("JAVA_HOME")
        arguments.jvmTarget = "1.8"
        arguments.classpath =
            "${jvmStdLibPaths.joinToString(postfix = ":", separator = ":")}:${System.getProperty("java.class.path")}"
        IncrementalCompilation.setIsEnabledForJvm(true)
    }

    private companion object {
        const val codeFile = "temp/code.kt"
        const val buildFilesDir = "temp/buildFiles"

        val jvmStdLibPaths = listOf(
            getStdLibPath("kotlin-stdlib"),
            getStdLibPath("kotlin-stdlib-common"),
            getStdLibPath("kotlin-test"),
            getStdLibPath("kotlin-test-common"),
            getStdLibPath("kotlin-reflect")
        )

        fun getStdLibPath(libToSearch: String): String {
            val kotlinVersion =
                File("build.gradle")
                    .readText().lines()
                    .firstOrNull { it.trim().startsWith("kotlinVersion") }
                    ?: throw Exception("There's no kotlinVersion parameter in build.gradle file.")
            val ver = kotlinVersion.split("=").last().trim().filter { it != '\'' }
            val gradleDir = "${System.getProperty("user.home")}/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/"
            val dir =
                File("$gradleDir/$libToSearch")
                    .listFiles()
                    ?.find { it.isDirectory && it.name.trim() == ver }?.path
                    ?: ""
            val pathToLib = File(dir).walkTopDown().find { it.name == "$libToSearch-$ver.jar" }?.absolutePath ?: ""
            require(pathToLib.isNotEmpty())
            return pathToLib
        }
    }

    private val logger: Logger = Logger.getLogger("debugLogger")

}
