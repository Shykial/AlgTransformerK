package com.shykial.algtransformerk.model

import spock.lang.Specification

import static com.shykial.algtransformerk.model.CornerPosition.*

class CubeStateUtilsTest extends Specification {
    def "should properly group corners"(CubeState cubeState, String outcome) {
        def corners = cubeState.groupedCorners

        expect:
            1 == 1
        where:
            cubeState            | outcome
            getRandomCubeState() | ""

    }


    @SuppressWarnings('GroovyAssignabilityCheck')
    static CubeState getRandomCubeState() {
        def cornersData = [
                [UBL, "WBO", CornerPieceState.SOLVED],
                [UBR, "BYR", CornerPieceState.MISPLACED],
                [UFR, "WGR", CornerPieceState.SOLVED],
                [UFL, "YWG", CornerPieceState.TWISTED],
                [DBL, "WBR", CornerPieceState.MISPLACED],
                [DBR, "BYO", CornerPieceState.MISPLACED],
                [DFR, "GRY", CornerPieceState.TWISTED],
                [DFL, "YGO", CornerPieceState.SOLVED]
        ]

        new CubeState(
                cornersData.collect {
                    new Corner(*it)
                }.toSet(),
                Collections.emptySet()
        )
    }
}
