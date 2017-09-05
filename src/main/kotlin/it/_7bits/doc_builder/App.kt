package it._7bits.doc_builder

import com.beust.jcommander.JCommander
import it._7bits.doc_builder.readers.GitFilesReader
import it._7bits.doc_builder.readers.LocalFilesReader

class App {
    companion object {
        private val log = logger()

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

            val doc = Documentation(
                    source = options.source,
                    target = options.target,
                    fileReader = if (options.git) {
                        GitFilesReader()
                    } else {
                        LocalFilesReader()
                    },
                    fileNameBuilder = FileNameBuilder(pattern = options.pattern),
                    writer = JadeWriter("templates/layout"),
                    indexWriter = JadeWriter("templates/index"),
                    renderer = MarkdownRenderer()
            )

            doc.build()

            if (options.server) {
                StaticServer.start(filesPath = options.target.toAbsolutePath().toString())
            }
        }
    }
}