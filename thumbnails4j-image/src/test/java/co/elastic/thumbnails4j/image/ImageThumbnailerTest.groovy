package co.elastic.thumbnails4j.image

import co.elastic.thumbnails4j.core.Dimensions
import spock.lang.Specification

import javax.imageio.ImageIO

class ImageThumbnailerTest extends Specification {

    def "test thumbnailing"() {
        setup:
        File inputFile = new File("src/test/resources/test-input.png")
        File thumbnail = new File("src/test/resources/test-output.png")
        def thumbnailer = new ImageThumbnailer()
        def dimensions = [new Dimensions(100,100)]

        when:
        def output = thumbnailer.getThumbnails(inputFile, dimensions)
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ImageIO.write(output[0], "png", baos)
        byte[] actualBytes = baos.toByteArray()
        byte[] expectedBytes = thumbnail.getBytes()

        then:
        output.size() == 1
        output[0].getWidth() == 85
        output[0].getHeight() == 100
        actualBytes.size() == expectedBytes.size()
        actualBytes == expectedBytes

    }
}
