package it._7bits.doc_builder

import com.beust.jcommander.IStringConverter
import com.beust.jcommander.Parameter
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

class Options {
    @Parameter(
            names = arrayOf("--source", "-s"),
            description = "Path to the Features location",
            required = false,
            converter = PathConverter::class
    )
    var source: Path = Paths.get("Features")

    @Parameter(
            names = arrayOf("--destination", "-d"),
            description = "Path to the destination location",
            required = false,
            converter = PathConverter::class
    )
    var destination: Path = Paths.get("doc_build")

    @Parameter(
            names = arrayOf("--server"),
            description = "Run server to host static ot not",
            required = false
    )
    var server: Boolean = false

    @Parameter(
            names = arrayOf("--git"),
            description = "Use git and generate as many API versions as many git refs exists",
            required = false
    )
    var git: Boolean = false

    @Parameter(
            names = arrayOf("--pattern"),
            description = "File name extractor pattern. Read how java Matcher and Pattern works",
            required = false,
            converter = PatternConverter::class
    )
    var pattern: Pattern = Pattern.compile(".*/(.*?)/doc/index\\.md")

    @Parameter(
            names = arrayOf("--help", "--usage"),
            description = "Display the help"
    )
    var usage: Boolean = false
}

class PathConverter: IStringConverter<Path?> {
    override fun convert(value: String?): Path? {
        if (value == null) return null
        return Paths.get(value)
    }

}

class PatternConverter: IStringConverter<Pattern?> {
    override fun convert(value: String?): Pattern? {
        if (value == null) return null
        return Pattern.compile(value)
    }
}