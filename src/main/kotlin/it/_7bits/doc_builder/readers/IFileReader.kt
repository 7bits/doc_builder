package it._7bits.doc_builder.readers

import java.io.InputStreamReader
import java.nio.file.Path

interface IFileReader {
    fun all(source: Path): Sequence<Doc>
}

data class Doc(
        val path: Path,
        val reader: InputStreamReader
)