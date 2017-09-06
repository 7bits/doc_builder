package it._7bits.doc_builder

import com.beust.jcommander.JCommander
import it._7bits.doc_builder.readers.GitFilesReader
import it._7bits.doc_builder.readers.LocalFilesReader
import it._7bits.doc_builder.versions.DummyVersionGenerator
import it._7bits.doc_builder.versions.GitVersionsGenerator
import java.nio.file.Paths

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

            val generator = if (options.git) GitVersionsGenerator() else DummyVersionGenerator()
            val fileNameBuilder = FileNameBuilder(pattern = options.pattern)
            val writer = JadeWriter("templates/layout")
            val indexWriter = JadeWriter("templates/index")
            val indexRenderer = IndexRenderer(indexWriter)
            val renderer = MarkdownRenderer()

            val serverThread = Thread({
                if (options.server) {
                    StaticServer.start(filesPath = options.destination.toAbsolutePath().toString())
                }
            })
            serverThread.start()

            val versions = generator.versions(source = options.source, destination = options.destination)
            versions.forEach { destination ->
                log.info("Version: ${destination.fileName}")
                val fileReader = if (options.git) GitFilesReader(destination.fileName.toString()) else LocalFilesReader()
                val doc = Documentation(
                        source = options.source,
                        destination = destination,
                        fileReader = fileReader,
                        fileNameBuilder = fileNameBuilder,
                        writer = writer,
                        renderer = renderer
                )

                val docs = doc.build()
                indexRenderer.createIndex(docs.map { destination.fileName.resolve(it) }, destination)
            }

            indexRenderer.createIndex(versions.map { it.fileName }, options.destination)


            serverThread.join()
        }
    }
}