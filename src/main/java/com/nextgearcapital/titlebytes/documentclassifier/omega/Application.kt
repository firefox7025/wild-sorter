package com.nextgearcapital.titlebytes.documentclassifier.omega

import java.io.IOException

object Application {

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val inputdir = args[0]
        val outdir = args[1]
        val fileFinderService = FileFinderService()
        val omegaTitleClassifier = OmegaTitleClassifier()
        val fileService = FileService()
        val files = fileFinderService.findFiles(inputdir)
        files.stream()
                .forEach() {x ->
                    run {
                        val classifyImage = omegaTitleClassifier.classifyImage(x)
                        fileService.move(outdir, x, classifyImage);
                    }
                }
    }
}