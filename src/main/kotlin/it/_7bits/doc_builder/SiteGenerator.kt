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
        versions.forEach { version ->
            log.info("Version: $version")
            val fileReader = if (options.git) GitFilesReader(version.toString()) else LocalFilesReader()
            val fullPath = if (options.git) options.destination.resolve(version) else version
            val doc = Documentation(
                    source = options.source,
                    destination = fullPath,
                    fileReader = fileReader,
                    fileNameBuilder = fileNameBuilder,
                    writer = writer,
                    renderer = renderer
            )

            val docs = doc.build()
            indexRenderer.createIndex(docs.map { version.resolve(it) }, fullPath, false)
        }

        if (options.git) {
            indexRenderer.createIndex(versions.map { it }, options.destination, true)
        }
    }
}