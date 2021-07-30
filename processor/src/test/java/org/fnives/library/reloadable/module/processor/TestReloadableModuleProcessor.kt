package org.fnives.library.reloadable.module.processor

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestReloadableModuleProcessor {

    @Test
    fun testSimpleSetup() {
        val userModuleInput = readResourceFileToString("simple/input/UserModuleInject.kt")
        val providedByUserModule = readResourceFileToString("simple/input/ProvidedByUserModule.kt")
        val fooDependency = readResourceFileToString("simple/input/FooDependency.kt")

        val result = KotlinCompilation().apply {
            sources = listOf(
                SourceFile.kotlin("UserModule.kt", userModuleInput),
                SourceFile.kotlin("ProvidedByUserModule.kt", providedByUserModule),
                SourceFile.kotlin("FooDependency.kt", fooDependency)
            )

            annotationProcessors = listOf(ReloadableModuleProcessor())

            inheritClassPath = true
            messageOutputStream = System.out // see diagnostics in real time
        }.compile()

        val generatedFiles = result.generatedFiles.toList()
        val reloadableUserModule = generatedFiles[0].readText()
        val reloadableUserModuleImpl = generatedFiles[1].readText()
        AssertionsAssertEqualsIgnoringMultipleSpaces(
            readResourceFileToString("simple/expected/ReloadUserModuleInjectModule.java"),
            reloadableUserModule
        )
        AssertionsAssertEqualsIgnoringMultipleSpaces(
            readResourceFileToString("simple/expected/ReloadUserModuleInjectModuleImpl.java"),
            reloadableUserModuleImpl
        )
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    companion object {
        private fun String.replaceSpacesWithOnlyOne(): String = replace("  *".toRegex(), " ")

        @Suppress("TestFunctionName")
        private fun AssertionsAssertEqualsIgnoringMultipleSpaces(expected: String, actual: String) =
            Assertions.assertEquals(expected.replaceSpacesWithOnlyOne(), actual.replaceSpacesWithOnlyOne())
    }
}
