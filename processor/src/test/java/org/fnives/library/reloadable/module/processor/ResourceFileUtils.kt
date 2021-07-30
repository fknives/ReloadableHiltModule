package org.fnives.library.reloadable.module.processor

import java.io.File
import java.nio.file.Paths

/**
+ * Helper class which read the file in the resources folder with the given [fileName] into a string, each line separated with [lineDelimiter].
+ */
fun Any.readResourceFileToString(fileName: String): String {
    val path = this::class.java.classLoader.getResource(fileName).toURI().path
    return File(Paths.get(path).toUri()).readText()
}
