package it._7bits.doc_builder

import it._7bits.doc_builder.readers.IFileReader
import it._7bits.doc_builder.readers.LocalFilesReader
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

// TODO: somehow understand that we iterate over git branches
class Documentation(
        private val source: Path,
        private val destination: Path,
        private val fileReader: IFileReader = LocalFilesReader(),
        private val writer: IWriter = JadeWriter("templates/layout"),
        private val renderer: IRenderer = MarkdownRenderer(),
        private val fileNameBuilder: FileNameBuilder = FileNameBuilder()
) {
    private val log = logger()

    fun build(): Sequence<Path> {
        if (!Files.exists(source)) {
            log.debug("Path '${source.toAbsolutePath()}' does not exists.")
            return emptySequence()
        }
        try {
            Files.createDirectories(destination)
            Files.list(destination).forEach {
                Files.delete(it)
            }
        } catch (e: Exception) {
            log.error("Can't create destination at path '${destination.toAbsolutePath()}'.", e)
        }

        val docs = fileReader.all(source).mapNotNull {
            try {
                log.info(it.path.toString())
                val content = renderer.render(it.reader)
                val file = createFile(it.path, destination)
                writer.write(mapOf("content" to content), file.writer())
                Paths.get(file.name)
            } catch (e: Exception) {
                log.warn("Can't convert file '$it'.", e)
                null
            }
        }

        return docs
    }

    private fun createFile(path: Path, target: Path): File {
        val fname = fileNameBuilder.build(path)
        val file = target.resolve(fname).toFile()
        file.createNewFile()
        return file
    }
}