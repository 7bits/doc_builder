package it._7bits.doc_builder

import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.BiPredicate
import kotlin.streams.toList

object Documentation {
    private val writer = JadeWriter("templates/layout")
    private val indexWriter = JadeWriter("templates/index")
    private val glob = FileSystems.getDefault().getPathMatcher("glob:**.{md,markdown}")
    private val renderer = MarkdownRenderer()

    fun build(source: Path, target: Path) {
        if (!Files.exists(source)) {
            println("Path '${source.toAbsolutePath()}' does not exists.")
            return
        }
        try {
            Files.createDirectories(target)
            Files.list(target).forEach {
                Files.delete(it)
            }
        } catch (e: Exception) {
            println("can't create target at path '${target.toAbsolutePath()}'")
            e.printStackTrace()
        }

        val docs = Files.find(source, 4, BiPredicate<Path, BasicFileAttributes> { t, _ ->
            glob.matches(t)
        }).toList().mapNotNull {
            try {
                println(it.toAbsolutePath())
                val content = renderer.render(it.toFile().reader())
                val file = createFile(it, target)
                writer.write(mapOf("content" to content), file.writer())
                file.name
            } catch (e: Exception) {
                println("Can't convert file '${it.toAbsolutePath()}'")
                e.printStackTrace()
                null
            }
        }

        createIndex(docs, target)
    }

    private fun createFile(path: Path, target: Path): File {
        val fname = "${path.parent.fileName}_${path.fileNameWithoutExt()}.html"
        val file = target.resolve(fname).toFile()
        file.createNewFile()
        println(fname)
        return file
    }

    private fun createIndex(docs: List<String>, target: Path) {
        try {
            val file = target.resolve("index.html").toFile()
            file.createNewFile()
            indexWriter.write(mapOf("docs" to docs), file.writer())
        } catch (e: Exception) {
            println("Can't create index file.")
            e.printStackTrace()
        }
    }
}