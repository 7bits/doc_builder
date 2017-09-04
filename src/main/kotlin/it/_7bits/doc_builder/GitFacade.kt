package it._7bits.doc_builder

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.nio.file.Path

object GitFacade {
    fun branches(path: Path): List<String> {
        val gitDir = path.resolve(".git")
        val repo = FileRepositoryBuilder().setGitDir(gitDir.toFile()).build()
        val git = Git(repo)
        return git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call()
                .toList().filterNotNull().map { it.name }
    }
}