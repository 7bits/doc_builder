package it._7bits.doc_builder

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import de.neuland.jade4j.JadeConfiguration
import de.neuland.jade4j.template.ClasspathTemplateLoader
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.BiPredicate
import kotlin.streams.toList

object Documentation {
    val jade = JadeConfiguration().apply {
        templateLoader = ClasspathTemplateLoader()
    }
    val glob = FileSystems.getDefault().getPathMatcher("glob:**.{md,markdown}")
    val parser = Parser.builder().build()
    val renderer = HtmlRenderer.builder().build()


    fun build(source: Path, target: Path) {
        val template = jade.getTemplate("templates/layout")

        if (!Files.exists(source)) {
            println("Path '${source.toAbsolutePath()}' does not exists.")
            return
        }
        try {
            Files.createDirectories(target)
        } catch (e: Exception) {
            println("can't create target at path '${target.toAbsolutePath()}'")
            e.printStackTrace()
        }

        val docs = Files.find(source, 4, BiPredicate<Path, BasicFileAttributes> { t, _ ->
            glob.matches(t)
        }).toList().map {
            try {
                println(it.toAbsolutePath())
                val node = parser.parseReader(it.toFile().reader())
                val content = renderer.render(node)

                val fname = it.parent.fileName.toString() + ".html"
                val file = target.resolve(fname).toFile()
                file.createNewFile()

                val writer = file.writer()
                jade.renderTemplate(template, mapOf("content" to content), writer)
                writer.flush()
                writer.close()
                fname
            } catch (e: Exception) {
                println("Can't convert file '${it.toAbsolutePath()}'")
                e.printStackTrace()
                null
            }
        }.filterNotNull()

        createIndex(docs, target)

    }

    fun createIndex(docs: List<String>, target: Path) {
        try {
            val template = jade.getTemplate("templates/index")
            val file = target.resolve("index.html").toFile()
            file.createNewFile()
            val writer = file.writer()
            jade.renderTemplate(template, mapOf("docs" to docs), writer)
            writer.flush()
            writer.close()
        }catch (e: Exception) {
            println("Can't create index file.")
            e.printStackTrace()
        }
    }
}