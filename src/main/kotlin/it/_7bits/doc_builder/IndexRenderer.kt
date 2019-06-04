package it._7bits.doc_builder

import java.nio.file.Path

class IndexRenderer(
        private val indexWriter: IWriter
) {
    private val log = logger()

    fun createIndex(docs: Sequence<Path>, target: Path, useFullReferencePath: Boolean) {
        try {
            val file = target.resolve("index.html").toFile()
            file.createNewFile()
            indexWriter.write(mapOf(
                    "docs" to docs.toList(),
                    "isFullPathUsed" to useFullReferencePath
            ), file.writer())
        } catch (e: Exception) {
            log.error("Can't create index file.", e)
        }
    }
}