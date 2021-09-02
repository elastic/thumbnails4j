package co.elastic.thumbnails4j.pdf

import co.elastic.thumbnails4j.core.Dimensions
import co.elastic.thumbnails4j.core.Thumbnailer
import spock.lang.Specification

import javax.imageio.ImageIO

class PDFThumbnailerTest extends Specification {
    def "test .pdf thumbnailing"(){
        setup:
        File inputFile = new File("src/test/resources/test-input.pdf")
        File thumbnail = new File("src/test/resources/test-output.jpg")
        Thumbnailer thumbnailer = new PDFThumbnailer()

        when:
        def output = thumbnailer.getThumbnails(inputFile, [new Dimensions(100, 100)])
        def baos = new ByteArrayOutputStream()
        ImageIO.write(output[0], "jpg", baos)
        baos.flush()
        baos.close()
        byte[] actual_bytes = baos.toByteArray()

        then:
        output.size() == 1
        thumbnail.getBytes() == actual_bytes
    }
}
