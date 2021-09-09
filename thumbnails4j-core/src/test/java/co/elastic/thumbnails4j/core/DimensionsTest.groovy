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

class DimensionsTest extends Specification {

    def "test toString"(){
        setup:
        Dimensions dimensions = new Dimensions(2,3)

        expect:
        dimensions.toString() == "[2, 3]"
    }

    def "test equality and hash code"(){
        expect:
        (dimension1 == dimension2) == is_equal
        (dimension1.hashCode() == dimension2.hashCode()) == hash_is_equal

        where:
        dimension1 | dimension2 | is_equal | hash_is_equal
        new Dimensions(1,2) | new Dimensions(2,3) | false | false
        new Dimensions(2,3) | new Dimensions(1,2) | false | false
        new Dimensions(1,2) | new Dimensions(1,2) | true | true

    }
}
