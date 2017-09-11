package it._7bits.doc_builder

import it._7bits.doc_builder.readers.GitFilesReader
import it._7bits.doc_builder.readers.LocalFilesReader
import it._7bits.doc_builder.versions.IVersionGenerator

object SiteGenerator {
    private val log = logger()

    fun generate(
            generator: IVersionGenerator,
            options: Options,
            fileNameBuilder: FileNameBuilder,
            writer: IWriter,
            renderer: IRenderer,
            indexRenderer: IndexRenderer
    ) {
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
    }
}