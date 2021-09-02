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
