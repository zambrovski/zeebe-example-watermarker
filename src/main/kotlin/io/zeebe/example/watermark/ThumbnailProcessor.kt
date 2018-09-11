package io.zeebe.example.watermark

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.imgscalr.Scalr
import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.ImageIO

@Component
class ThumbnailProcessor : Processor {

    companion object {
        const val THUMBNAIL_WIDTH = 50
        const val THUMBNAIL_HEIGHT = 50
    }

    override fun process(exchange: Exchange) {

        val fis = exchange.getIn().getBody(InputStream::class.java)
        val baos = ByteArrayOutputStream()

        val source = ImageIO.read(fis)
        val thumbnail = createThumbnail(source)
        ImageIO.write(thumbnail, "png", baos)

        exchange.getIn().body = baos
        exchange.getIn().headers[Exchange.FILE_NAME] = "thumb_${exchange.getIn().headers[Exchange.FILE_NAME]}"
    }

    fun createThumbnail(image: BufferedImage): BufferedImage {
        return Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT)
    }
}