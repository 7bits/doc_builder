package it._7bits.doc_builder

import spark.Spark

/**
 * Simple static server runner to host recently generated documentation.
 */
object StaticServer {
    private val log = logger()

    fun start(filesPath: String, port: Int = 8888) {
        log.info("Start server for hosting documentation on port: $port")
        Spark.port(port)
        Spark.staticFiles.externalLocation(filesPath)
        Spark.init()
    }
}