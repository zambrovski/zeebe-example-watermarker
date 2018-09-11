package io.zeebe.example.watermark

import mu.KLogging
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.imgscalr.Scalr
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import javax.annotation.PostConstruct
import javax.imageio.ImageIO


@Component
class WatermarkingProcessor : Processor {

    companion object : KLogging()

    @Value("\${zeebe.watermark}")
    private lateinit var watermarkFilename: String

    private lateinit var watermark: BufferedImage

    @PostConstruct
    fun readWatermark() {
        watermark = ImageIO.read(File(watermarkFilename))
        logger.info { "Using watermark $watermarkFilename" }
    }

    override fun process(exchange: Exchange) {

        val fis = exchange.getIn().getBody(InputStream::class.java)
        val out = ByteArrayOutputStream()

//        val sourceImageStream = ImageIO.createImageInputStream(fis)
//        val sourceReader = ImageIO.getImageReaders(sourceImageStream).next().apply { input = sourceImageStream }


//         logger.info { "Format ${sourceReader.formatName}" }

        val source = ImageIO.read(fis)
        markImage(source, watermark)

        ImageIO.write(source, "png", out)

        exchange.getIn().body = out

    }

    fun markImage(image: BufferedImage, watermark: BufferedImage) {
        val watermarkWidth = image.width / 3
        val scalingFactor = watermarkWidth.toDouble() / watermark.width
        val watermarkHeight = (watermark.height * scalingFactor).toInt()

        val graphics = image.graphics as Graphics2D

        val resizedWatermark = Scalr.resize(watermark, Scalr.Method.ULTRA_QUALITY, watermarkWidth, watermarkHeight)
        graphics.drawImage(
                resizedWatermark,
                image.width - watermarkWidth,
                image.height - watermarkHeight,
                null)
    }
}