package com.nextgearcapital.titlebytes.documentclassifier.omega

import com.nextgearcapital.titlebytes.documentclassifier.omega.domain.State
import org.apache.commons.io.FileUtils
import org.datavec.image.loader.Java2DNativeImageLoader
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler
import java.awt.image.BufferedImage
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.imageio.ImageIO

class OmegaTitleClassifier {
    private var multiLayerNetwork: ComputationGraph? = null
    private var labelMap: HashMap<Int, State>? = null
    @Throws(IOException::class)
    fun classifyImage(path: Path): State {
        return classifyImage(ImageIO.read(path.toFile()))
    }

    /**
     * Classification of a native Java BufferedImage
     *
     *
     * Load the image, given a height, width and channel.
     * Use the same preprocessor used in the training repo.
     *
     * @param bufferedImage Just a native buffered image RGB style, AGB will throw it off.
     * @return State Abbreviation
     */
    fun classifyImage(bufferedImage: BufferedImage?): State {
        return try {
            val loader = Java2DNativeImageLoader(height, width, channel)
            val image = loader.asMatrix(bufferedImage)
            val scaler: DataNormalization = ImagePreProcessingScaler()
            scaler.transform(image)
            this!!.decode(multiLayerNetwork!!.output(false, image))!!
        } catch (e: Exception) {
            State.UNKNOWN
        }
    }

    /**
     * Quick Iteration parsing network output, comparing evaluation by label.
     *
     * @param networkOutput The results from the model
     * @return The top value of the network.
     */
    private fun decode(networkOutput: Array<INDArray>): State? {
        var label = 0
        var maxPredictionValue = 0f
        for (i in 0 until networkOutput[0].columns()) {
            val result = networkOutput[0].getColumn(i.toLong()).getFloat(0)
            if (result > maxPredictionValue) {
                label = i
                maxPredictionValue = result
            }
        }
        return if (maxPredictionValue > 0.70) {
            labelMap!![label]
        } else {
            State.UNKNOWN
        }
    }

    /**
     * Private function to map the labels of the model to a HashMap allowing for a O(1) lookups.
     *
     * @param labels labels from the model posted and downloaded on build
     * @return A Hashmap with the label id and state abbreviation.
     */
    private fun labelMap(labels: ArrayList<String>): HashMap<Int, State> {
        val labelMap = HashMap<Int, State>()
        for (i in labels.indices) {
            labelMap[i] = State.valueOfAbbreviation(labels[i])
        }
        return labelMap
    }

    companion object {
        private const val height = 224
        private const val width = 224
        private const val channel = 3
    }

    init {
        try {
            val resourceAsStream = javaClass.classLoader.getResourceAsStream("titlesClassifier/titlesClassifier.zip")
            ClassLoader::class.java.getResourceAsStream("titlesClassifier/titlesClassifier.zip")
            val loadedNetwork = Files.createTempFile("network", "zip").toFile()
            FileUtils.copyInputStreamToFile(resourceAsStream, loadedNetwork)
            multiLayerNetwork = ModelSerializer.restoreComputationGraph(loadedNetwork, false)
            val labels = ModelSerializer.getObjectFromFile<ArrayList<String>>(loadedNetwork, "labels")
            labelMap = labelMap(labels)
        } catch (e: Exception) {
            throw RuntimeException("Unable to load classifier or labels", e)
        }
    }
}