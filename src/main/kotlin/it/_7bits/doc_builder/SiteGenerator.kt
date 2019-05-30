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
                    renderer = renderer,
                    pattern = options.pattern
            )

            val docs = doc.build()
            indexRenderer.createIndex(docs.map { destination.fileName.resolve(it) }, destination)
        }

//        TODO: disabled for reason described below
//        As for now, DocBuilder is unable to go through all git branches and generate documentations.
//        val versions contains only one version. In the end, index.html with wrong reference ends up being broken.
//        This feature needs to be fixed, but not now. As for now, we need to have functional index.html
//        indexRenderer.createIndex(versions.map { it.fileName }, options.destination)
    }
}