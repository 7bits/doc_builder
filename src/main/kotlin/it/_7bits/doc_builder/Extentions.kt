package it._7bits.doc_builder

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

fun Path.fileNameWithoutExt(): String {
    return this.fileName.toString().split(".").dropLast(1).joinToString(".")
}

fun Any.logger(): Logger = LoggerFactory.getLogger(this::class.java)