package it._7bits.doc_builder

import com.beust.jcommander.IStringConverter
import com.beust.jcommander.Parameter
import java.nio.file.Path
import java.nio.file.Paths

class Options {
    @Parameter(
            names = arrayOf("--source", "-s"),
            description = "Path to the Features location",
            required = false,
            converter = PathConverter::class
    )
    var source: Path = Paths.get("Features")

    @Parameter(
            names = arrayOf("--target", "-t"),
            description = "Path to the target location",
            required = false,
            converter = PathConverter::class
    )
    var target: Path = Paths.get("doc_build")

    @Parameter(
            names = arrayOf("--server"),
            description = "Run server to host static ot not",
            required = false
    )
    var server: Boolean = false

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