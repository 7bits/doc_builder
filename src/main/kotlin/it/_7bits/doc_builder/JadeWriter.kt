package it._7bits.doc_builder

import de.neuland.jade4j.JadeConfiguration
import de.neuland.jade4j.template.ClasspathTemplateLoader
import java.io.Writer

class JadeWriter(
        private val templatePath: String
): IWriter {
    private val jade = JadeConfiguration().apply {
        templateLoader = ClasspathTemplateLoader()
    }
    private val template = jade.getTemplate(templatePath)

    override fun write(content: Map<String, Any>, writer: Writer) {
        jade.renderTemplate(template, content, writer)
        writer.flush()
        writer.close()
    }
}

interface IWriter {
    fun write(content: Map<String, Any>, writer: Writer)
}