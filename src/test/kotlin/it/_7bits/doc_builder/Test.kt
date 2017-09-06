package it._7bits.doc_builder

import org.junit.Test
import java.nio.file.Paths

class Test {
    @Test
    fun Should_Work() {
        val path = Paths.get("/Users/ilya/Documents/work/steptodream/coaching-platform-mvp-back")
        (GitFacade.branches(path) + GitFacade.tags(path)).forEach {
            println(it)
        }
    }
}