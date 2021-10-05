package com.shykial.algtransformerk.helpers


import spock.lang.Specification

class MutableRotationTest extends Specification {

    def "should properly add rotations #rotations to initial state"(String rotations, Map<String, String> outcome) {
        given:
            MutableRotation rotation = new MutableRotation()
        when:
            rotations.split(" ").each(rotation::addRotation)
        then:
            rotation.state == outcome.collectEntries { k, v -> [k.toCharacter(), v.toCharacter()] }

        where:
            rotations                         | outcome
            "x y"                             | ['F': 'R',
                                                 'D': 'B',
                                                 'B': 'L',
                                                 'U': 'F',
                                                 'L': 'D',
                                                 'R': 'U']

            "x"                               | ['U': 'F',
                                                 'F': 'D',
                                                 'D': 'B',
                                                 'B': 'U',
                                                 'R': 'R',
                                                 'L': 'L']

            "x' x2 x' x' x'"                  | ['U': 'D',
                                                 'F': 'B',
                                                 'D': 'U',
                                                 'B': 'F',
                                                 'R': 'R',
                                                 'L': 'L']

            "z'"                              | ['U': 'R',
                                                 'R': 'D',
                                                 'D': 'L',
                                                 'L': 'U',
                                                 'F': 'F',
                                                 'B': 'B']

            "y' y2"                           | ['F': 'R',
                                                 'L': 'F',
                                                 'B': 'L',
                                                 'R': 'B',
                                                 'U': 'U',
                                                 'D': 'D']

            "x y2 z z2 x y2 z' y x z2 y2 x y" | ['U': 'F',
                                                 'F': 'R',
                                                 'D': 'B',
                                                 'B': 'L',
                                                 'L': 'D',
                                                 'R': 'U']
    }

}
