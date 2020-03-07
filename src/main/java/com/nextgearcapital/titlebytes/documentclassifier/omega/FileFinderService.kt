package com.nextgearcapital.titlebytes.documentclassifier.omega

import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*

class FileFinderService {
    private val glob = "glob:**/*.jpg"
    @Throws(IOException::class)
    fun findFiles(basePath: String?): Set<Path> {
        val pathMatcher = FileSystems.getDefault().getPathMatcher(
                glob)
        val files: MutableSet<Path> = HashSet()
        Files.walkFileTree(Paths.get(basePath), object : SimpleFileVisitor<Path>() {
            @Throws(IOException::class)
            override fun visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult {
                if (pathMatcher.matches(path)) {
                    println(path)
                    files.add(path)
                }
                return FileVisitResult.CONTINUE
            }

            @Throws(IOException::class)
            override fun visitFileFailed(file: Path, exc: IOException): FileVisitResult {
                return FileVisitResult.CONTINUE
            }
        })
        return files
    }
}