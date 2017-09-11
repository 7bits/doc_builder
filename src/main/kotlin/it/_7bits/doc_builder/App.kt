package it._7bits.doc_builder

import com.beust.jcommander.JCommander
import it._7bits.doc_builder.versions.DummyVersionGenerator
import it._7bits.doc_builder.versions.GitVersionsGenerator

class App {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val options = Options()
            val jc = JCommander.newBuilder().addObject(options).build().apply {
                programName = "Document builder"
            }

            jc.parse(*args)

            if (options.usage) {
                jc.usage()
                return
            }

            val generator = if (options.git) GitVersionsGenerator() else DummyVersionGenerator()
            val fileNameBuilder = FileNameBuilder(pattern = options.pattern)
            val writer = JadeWriter("templates/layout")
            val indexWriter = JadeWriter("templates/index")
            val indexRenderer = IndexRenderer(indexWriter)
            val renderer = MarkdownRenderer()

            val serverThread = if (options.server) {
                Thread({
                    StaticServer.start(filesPath = options.destination.toAbsolutePath().toString())
                })
            } else null
            serverThread?.start()

            SiteGenerator.generate(generator, options, fileNameBuilder, writer, renderer, indexRenderer)

            serverThread?.join()
        }
    }
}