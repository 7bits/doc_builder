package it._7bits.doc_builder

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import java.io.Reader

class MarkdownRenderer: IRenderer {
    private val parser = Parser.builder().build()
    private val renderer = HtmlRenderer.builder().build()

    override fun render(reader: Reader): String {
        val node = parser.parseReader(reader)
        return renderer.render(node)
    }
}

interface IRenderer {
    fun render(reader: Reader): String
}