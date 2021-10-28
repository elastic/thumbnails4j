/*
 *
 *  * Licensed to Elasticsearch B.V. under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Elasticsearch B.V. licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *	http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package co.elastic.thumbnails4j.image

import co.elastic.thumbnails4j.core.Dimensions
import spock.lang.Specification

import javax.imageio.ImageIO

class ImageThumbnailerTest extends Specification {

    def "test image thumbnailing"() {
        setup:
        File inputFile = new File("src/test/resources/test-input.png")
        File thumbnail = new File("src/test/resources/test-output.png")
        def thumbnailer = new ImageThumbnailer('png')
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
//        actualBytes.size() == expectedBytes.size()
//        actualBytes == expectedBytes

    }
}
