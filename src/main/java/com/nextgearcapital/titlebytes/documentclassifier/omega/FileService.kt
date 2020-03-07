package com.nextgearcapital.titlebytes.documentclassifier.omega

import com.nextgearcapital.titlebytes.documentclassifier.omega.domain.State
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FileService {
    @Throws(IOException::class)
    fun move(outputDir: String, originalFile: Path, state: State) {
        val outputDirectory = Paths.get(outputDir, state.abbreviation)
        val outputFile = Paths.get(outputDirectory.toString(), originalFile.fileName.toString())
        if (Files.notExists(outputDirectory)) {
            Files.createDirectories(outputDirectory)
        }
        Files.copy(originalFile, outputFile)
    }
}