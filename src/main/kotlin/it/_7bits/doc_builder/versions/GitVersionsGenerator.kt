package it._7bits.doc_builder.versions

import it._7bits.doc_builder.GitFacade
import java.nio.file.Path
import java.nio.file.Paths

class GitVersionsGenerator(
        val mode: Mode = Mode.ALL
) : IVersionGenerator {
    enum class Mode {ALL, TAGS, BRANCHES }

    override fun versions(source: Path, destination: Path): Sequence<Path> {
        return (GitFacade.branches(source) + GitFacade.tags(source)).map {
            Paths.get(it)
        }
    }
}