package it._7bits.doc_builder

import java.nio.file.Path
import java.util.regex.Pattern

class FileNameBuilder(
        private val pattern: Pattern = Pattern.compile(".*/(.*?)/doc/index\\.md")
) {
    fun build(path: Path): String {
        val matcher = pattern.matcher(path.toString())
        val name = if (matcher.find()) {
            (1..matcher.groupCount()).joinToString("_") { matcher.group(it) }
        } else {
            dummyFilename(path)
        }

        return "$name.html"
    }

    private fun dummyFilename(path: Path): String {
        return if (path.parent == null) {
            path.fileNameWithoutExt()
        } else {
            "${path.parent.fileName}_${path.fileNameWithoutExt()}"
        }
    }
}