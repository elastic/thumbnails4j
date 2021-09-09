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

package co.elastic.thumbnails4j.core

import spock.lang.Specification

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class ThumbnailUtilsTest extends Specification {

    def "test that it maintains the aspect ratio"(){
        setup:
        def inputFile = new File("src/test/resources/images/test.png")
        BufferedImage image = ImageIO.read(inputFile)

        when:
        def output = ThumbnailUtils.scaleImage(image, new Dimensions(target_width, target_height))

        then:
        output.width == output_width
        output.height == output_height

        where:
        target_width | target_height | output_width | output_height
        100 | 100 | 85 | 100
        100 | 1000 | 100 | 118
        1000 | 100 | 85 | 100
        1000 | 1000 | 850 | 1000
        510 | 600 | 510 | 600
        510 | 1000 | 510 | 600
        1000 | 600 | 510 | 600
    }
}
