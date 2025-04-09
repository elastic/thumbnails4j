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
//        thumbnail.getBytes() == actual_bytes
    }

    def "test .pdf thumbnailing bigger"(){
        setup:
        File inputFile = new File("src/test/resources/test-bigger.pdf")
        Thumbnailer thumbnailer = new PDFThumbnailer()

        when:
        def output = thumbnailer.getThumbnails(inputFile, [new Dimensions(100, 100)])
        def baos = new ByteArrayOutputStream()
        ImageIO.write(output[0], "jpg", baos)
        baos.flush()
        baos.close()

        then:
        output.size() == 1
    }
}
