package io.zeebe.example.watermark

import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Component
import java.io.InputStream
import javax.imageio.ImageIO

fun main(args: Array<String>) = SpringApplication.run(ImageProcessor::class.java, *args).let { Unit }

@SpringBootApplication
open class ImageProcessor

@Component
open class WatermarkerRouter : RouteBuilder() {

    @Value("\${zeebe.sourceDir}")
    private lateinit var sourceDir: String

    @Value("\${zeebe.destDir}")
    private lateinit var destDir: String

    @Autowired
    private lateinit var watermarker: WatermarkingProcessor
    @Autowired
    private lateinit var thumbnailer: ThumbnailProcessor

    fun sourceFile() = "file:$sourceDir?delete=false"
    fun destinationFile() = "file:$destDir"


    override fun configure() {
        from(sourceFile())
                .id("File processor")
                .to("direct:watermark")
                .to("direct:thumbnail")
        from("direct:watermark")
                .id("Watermark image")
                .log(LoggingLevel.INFO, "Watermarking file \${header.CamelFileName}")
                .process(watermarker)
                .to(destinationFile())

        from("direct:thumbnail")
                .id("Thumbnailing image")
                .log(LoggingLevel.INFO, "Thumbnailing file \${header.CamelFileName}")
                .process(thumbnailer)
                .to(destinationFile())
    }
}
