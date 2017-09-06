package it._7bits.doc_builder.versions

import java.nio.file.Path

class DummyVersionGenerator: IVersionGenerator {
    override fun versions(source: Path, destination: Path): Sequence<Path> = sequenceOf(destination)
}