package it._7bits.doc_builder

import com.beust.jcommander.JCommander

class App {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val options = Options()
            val jc = JCommander.newBuilder().addObject(options).build().apply {
                programName = "Document builder"
            }

            jc.parse(*args)

            if (options.usage) {
                jc.usage()
                return
            }

            Documentation.build(options.source, options.target)

            if (options.server) {
                StaticServer.start(filesPath = options.target.toAbsolutePath().toString())
            }
        }
    }
}