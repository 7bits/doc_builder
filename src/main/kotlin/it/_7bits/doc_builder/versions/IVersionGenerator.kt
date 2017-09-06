package it._7bits.doc_builder.versions

import java.nio.file.Path

interface IVersionGenerator {
    fun versions(source: Path, destination: Path): Sequence<Path>
}