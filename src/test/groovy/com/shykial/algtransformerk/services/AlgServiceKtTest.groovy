package com.shykial.algtransformerk.services

import kotlin.NotImplementedError
import spock.lang.Specification

@SuppressWarnings('GroovyAccessibility')
class AlgServiceKtTest extends Specification {
    def "should properly parse moves #output from input #input"() {
        expect:
            AlgServiceKt.readMoves(input).join(' ') == output

        where:
            input                              | output
            "R2 L2 [F' D2 R, F U2] (L' U2 F)3" | "R2 L2 F' D2 R F U2 R' D2 F U2 F' L' U2 F L' U2 F L' U2 F"
            "D2 U2 (R F2 D, F L) * 2 R"        | "D2 U2 R F2 D F L D' F2 R' L' F' R F2 D F L D' F2 R' L' F' R"
    }

    def "should fail parsing #input with NotImplementedError"() {
        when:
            AlgServiceKt.readMoves(input)
        then:
            thrown(NotImplementedError)

        where:
            input                  | _
            "D [F2 U2 R, (L D')3]" | _
            "R [D' U2 (F D2, F)]"  | _
    }
}
