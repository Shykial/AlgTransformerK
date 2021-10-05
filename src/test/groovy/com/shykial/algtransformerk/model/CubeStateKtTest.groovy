package com.shykial.algtransformerk.model

import spock.lang.Specification

import static com.shykial.algtransformerk.model.CornerPosition.*

class CubeStateKtTest extends Specification {
    def "should properly group corners"(CubeState cubeState) {
        when:
            def corners = cubeState.groupedCorners
        then:
            corners.size() == 3
            corners[CornerPieceState.MISPLACED] == Map.of(
                    UBR, 'BYR',
                    DBL, 'WBR',
                    DBR, 'BYO'
            )
            corners[CornerPieceState.TWISTED] == Map.of(
                    UFL, 'YWG',
                    DFR, 'GRY'
            )
            corners[CornerPieceState.SOLVED] == Map.of(
                    UBL, 'WBO',
                    UFR, 'WGR',
                    DFL, 'YGO'
            )

        where:
            cubeState            | _
            getRandomCubeState() | _

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

        new CubeState('',
                cornersData.collect {
                    new Corner(*it)
                }.toSet(),
                Collections.emptySet()
        )
    }
}
