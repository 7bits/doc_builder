package it._7bits.doc_builder

import it._7bits.doc_builder.readers.IFileReader
import it._7bits.doc_builder.readers.LocalFilesReader
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

// TODO: somehow understand that we iterate over git branches
class Documentation(
        private val source: Path,
        private val target: Path,
        private val fileReader: IFileReader = LocalFilesReader(),
        private val writer: IWriter = JadeWriter("templates/layout"),
        private val indexWriter: IWriter = JadeWriter("templates/index"),
        private val renderer: IRenderer = MarkdownRenderer(),
        private val fileNameBuilder: FileNameBuilder = FileNameBuilder()
) {
    private val log = logger()

    fun build() {
        if (!Files.exists(source)) {
            log.info("Path '${source.toAbsolutePath()}' does not exists.")
            return
        }
        try {
            Files.createDirectories(target)
            Files.list(target).forEach {
                Files.delete(it)
            }
        } catch (e: Exception) {
            log.error("Can't create target at path '${target.toAbsolutePath()}'.", e)
        }

        val docs = fileReader.all(source).mapNotNull {
            try {
                log.info(it.path.toString())
                val content = renderer.render(it.reader)
                val file = createFile(it.path, target)
                writer.write(mapOf("content" to content), file.writer())
                file.name
            } catch (e: Exception) {
                log.warn("Can't convert file '$it'.", e)
                null
            }
        }

        createIndex(docs, target)
    }

    private fun createFile(path: Path, target: Path): File {
        val fname = fileNameBuilder.build(path)
        val file = target.resolve(fname).toFile()
        file.createNewFile()
        return file
    }

    private fun createIndex(docs: Sequence<String>, target: Path) {
        try {
            val file = target.resolve("index.html").toFile()
            file.createNewFile()
            indexWriter.write(mapOf("docs" to docs.toList()), file.writer())
        } catch (e: Exception) {
            log.error("Can't create index file.", e)
        }
    }
}