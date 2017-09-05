package it._7bits.doc_builder.readers

import it._7bits.doc_builder.GitFacade
import org.eclipse.jgit.lib.Constants
import java.nio.file.FileSystems
import java.nio.file.Path

class GitFilesReader(private val target: String = Constants.HEAD): IFileReader {
    private val glob = FileSystems.getDefault().getPathMatcher("glob:**.{md,markdown}")

    override fun all(source: Path): Sequence<Doc> {
        return GitFacade.files(source, target).filter {
            glob.matches(it.path)
        }.map {
            Doc(it.path, it.load().reader())
        }
    }
}

