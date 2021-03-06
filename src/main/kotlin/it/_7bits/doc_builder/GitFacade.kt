package it._7bits.doc_builder

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.TreeWalk
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.Paths

object GitFacade {
    private val log = logger()

    fun branches(path: Path): Sequence<String> {
        val gitDir = path.resolve(".git")
        val repo = FileRepositoryBuilder().setGitDir(gitDir.toFile()).build()
        val git = Git(repo)
        return git.branchList()
                .setListMode(ListBranchCommand.ListMode.ALL)
                .call()
                .asSequence()
                .filterNotNull()
                .map { it.name }
                .map {
                    // TODO: I know it could be regexp but who cares
                    it
                            .replace("refs/heads/", "")
                            .replace("refs/remotes/", "")
                            .replace("/", "__")
                }.filterNot { it == "HEAD" }
    }

    fun tags(path: Path): Sequence<String> {
        val gitDir = path.resolve(".git")
        val repo = FileRepositoryBuilder().setGitDir(gitDir.toFile()).build()
        val git = Git(repo)
        return git.tagList()
                .call()
                .asSequence()
                .filterNotNull()
                .map { it.name }
                .map {
                    it
                            .replace("refs/tags/", "")
                            .replace("/", "__")
                }
    }

    fun files(path: Path, target: String = Constants.HEAD): Sequence<GitFile> {
        val gitDir = path.resolve(".git")
        val repo = FileRepositoryBuilder().setGitDir(gitDir.toFile()).build()
        val lastCommitId = repo.resolve(target)

        val rw = RevWalk(repo)
        val tw = TreeWalk(repo)
        tw.isRecursive = true
        return try {
            val tree = rw.parseCommit(lastCommitId).tree
            tw.addTree(tree)
            KTreeWalk(tw, repo).asSequence()
        } catch (e: Exception) {
            log.error("Error during walking repo tree", e)
            emptySequence()
        } finally {
            tw.release()
            rw.dispose()
        }
    }

    data class GitFile(
            val path: Path,
            val objectId: ObjectId,
            private val repo: Repository
    ) {
        fun load(): InputStream {
            val loader = repo.open(objectId)
            return loader.openStream()
        }
    }

    /**
     * A work around and re-wrapping of TreeWalk class that has only `next()` method.
     * KTreeWalk implements Iterator, so map, filters and so on will be available now!
     */
    private class KTreeWalk(private val treeWalk: TreeWalk, private val repo: Repository) : Iterator<GitFile> {
        private var hasNext_ = false
        private var next_ = step()

        override fun hasNext(): Boolean {
            return hasNext_
        }

        override fun next(): GitFile {
            val now = next_
            next_ = step()
            return now
        }

        private fun step(): GitFile {
            hasNext_ = treeWalk.next()
            treeWalk.pathString
            return GitFile(Paths.get(treeWalk.pathString), treeWalk.getObjectId(0), repo)
        }
    }
}