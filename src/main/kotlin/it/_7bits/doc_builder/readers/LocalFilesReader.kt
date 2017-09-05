package it._7bits.doc_builder.readers

import it._7bits.doc_builder.logger
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.BiPredicate
import kotlin.streams.asSequence

class LocalFilesReader : IFileReader {
    private val log = logger()
    private val glob = FileSystems.getDefault().getPathMatcher("glob:**.{md,markdown}")

    override fun all(source: Path): Sequence<Doc> {
        return Files.find(source, 4, BiPredicate<Path, BasicFileAttributes> { t, _ ->
            glob.matches(t)
        }).asSequence().mapNotNull {
            Doc(it.toAbsolutePath(), it.toFile().reader())
        }
    }
}