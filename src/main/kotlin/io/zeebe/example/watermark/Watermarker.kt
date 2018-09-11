package io.zeebe.example.watermark

import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Component
import java.io.File
import java.io.InputStream

fun main(args: Array<String>) = SpringApplication.run(Watermarker::class.java, *args) as Unit

@SpringBootApplication
open class Watermarker

@Component
open class WatermarkerRouter : RouteBuilder() {

    @Value("\${zeebe.sourceDir}")
    private lateinit var sourceDir: String

    @Value("\${zeebe.destDir}")
    private lateinit var destDir: String

    @Autowired
    private lateinit var watermarker: WatermarkingProcessor

    fun sourceFile() = "file:" + sourceDir
    fun destinationFile() = "file:" + destDir


    override fun configure() {
        from(sourceFile())
                .id("Watermark image")
                .log(LoggingLevel.INFO, "Processing file")
                .process(watermarker)
                .to(destinationFile())
    }

}