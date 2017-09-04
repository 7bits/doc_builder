package it._7bits.doc_builder

import java.nio.file.Path

fun Path.fileNameWithoutExt(): String {
    return this.fileName.toString().split(".").dropLast(1).joinToString(".")
}