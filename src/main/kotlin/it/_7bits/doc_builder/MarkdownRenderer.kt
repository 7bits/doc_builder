package it._7bits.doc_builder

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import java.io.Reader

class MarkdownRenderer {
    private val parser = Parser.builder().build()
    private val renderer = HtmlRenderer.builder().build()

    fun render(reader: Reader): String {
        val node = parser.parseReader(reader)
        return renderer.render(node)
    }
}