package it._7bits.doc_builder

import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet
import java.io.Reader

class MarkdownRenderer: IRenderer {
    private val options = MutableDataSet().set(Parser.EXTENSIONS, listOf(TablesExtension.create()))
    private val parser = Parser.builder(options).build()
    private val renderer = HtmlRenderer.builder(options).build()

    override fun render(reader: Reader): String {
        val node = parser.parseReader(reader)
        return renderer.render(node)
    }
}

interface IRenderer {
    fun render(reader: Reader): String
}