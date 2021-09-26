package com.shykial.algtransformerk.services

import kotlin.NotImplementedError
import spock.lang.Specification

@SuppressWarnings('GroovyAccessibility')
class AlgServiceKtTest extends Specification {
    def "should properly parse moves #output from input #input"(String input, String output) {
        expect:
            AlgServiceKt.readMoves(input).join(' ') == output

        where:
            input                              | output
            "R2 L2 [F' D2 R, F U2] (L' U2 F)3" | "R2 L2 F' D2 R F U2 R' D2 F U2 F' L' U2 F L' U2 F L' U2 F"
            "D2 U2 (R F2 D, F L) * 2 R"        | "D2 U2 R F2 D F L D' F2 R' L' F' R F2 D F L D' F2 R' L' F' R"
            "Rw2 D' (R' L2 F)2 (F U2, L)2"     | "Rw2 D' R' L2 F R' L2 F F U2 L U2 F' L' F U2 L U2 F' L'"
    }

    def "should fail parsing #input with NotImplementedError"(String input) {
        when:
            AlgServiceKt.readMoves(input)
        then:
            thrown(NotImplementedError)

        where:
            input                  | _
            "D [F2 U2 R, (L D')3]" | _
            "R [D' U2 (F D2, F)]"  | _
    }

    def "should properly cancel #input to #output"(String input, String output) {
        given:
            List<String> moves = input.split(" ").toList()

        when:
            List<String> newMoves = AlgServiceKt.withCancellations(moves)

        then:
            newMoves.join(" ") == output

        where:
            input                                             | output
            "R2 U2 F' D D' F U L2 D2 D U2 F R2 R' L2 L' L R'" | "R2 U' L2 D' U2 F L2"
            "D F2 U' R' L2 R2 L' R L2 L R2 U F2 D R R U2"     | "D2 R2 U2"
            "F2 U F' L2 R' U D' R U2 D2 F2 L'"                | "F2 U F' L2 R' U D' R U2 D2 F2 L'"
    }
}
