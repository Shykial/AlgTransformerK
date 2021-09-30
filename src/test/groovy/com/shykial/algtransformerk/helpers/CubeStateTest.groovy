package com.shykial.algtransformerk.helpers


import com.shykial.algtransformerk.model.CornerPosition
import com.shykial.algtransformerk.model.EdgePosition
import groovy.transform.MapConstructor
import spock.lang.Specification

import static com.shykial.algtransformerk.model.EdgePosition.*
import static com.shykial.algtransformerk.model.CornerPosition.*

class CubeStateTest extends Specification {
    final static List<Scramble> SCRAMBLED_CUBES = [sampleScrambledCube1(), sampleScrambledCube2()]

    def "should properly add moves #scramble to initial cube state"(String scramble,
                                                                    Map<EdgePosition, String> expectedEdges,
                                                                    Map<CornerPosition, String> expectedCorners) {
        given:
            MutableCubeState cubeState = new MutableCubeState()
        when:
            scramble.split(" ").each(cubeState::makeMove)
        then:
            cubeState.edges == expectedEdges
            cubeState.corners == expectedCorners

        where:
            scramble                         || expectedEdges                    | expectedCorners
            SCRAMBLED_CUBES[0].scrambleMoves || SCRAMBLED_CUBES[0].expectedEdges | SCRAMBLED_CUBES[0].expectedCorners
            SCRAMBLED_CUBES[1].scrambleMoves || SCRAMBLED_CUBES[1].expectedEdges | SCRAMBLED_CUBES[1].expectedCorners
    }

    @MapConstructor
    static class Scramble {
        String scrambleMoves
        Map<EdgePosition, String> expectedEdges
        Map<CornerPosition, String> expectedCorners
    }

    static def sampleScrambledCube1() {
        String scramble = "R' D' B2 U' L2 U R2 D' B2 U' L2 F2 U B' F' R' F D U' L F'"
        def expectedEdges = [
                (UF): 'RW',
                (UL): 'GY',
                (UB): 'RG',
                (UR): 'WG',

                (FL): 'RB',
                (FR): 'OY',
                (BL): 'OG',
                (BR): 'WB',

                (DF): 'YR',
                (DR): 'BY',
                (DB): 'OW',
                (DL): 'BO'
        ]
        def expectedCorners = [
                (UFR): 'YRG',
                (UFL): 'OGY',
                (UBL): 'WOG',
                (UBR): 'WOB',
                (DFL): 'RWG',
                (DFR): 'BOY',
                (DBR): 'BWR',
                (DBL): 'BYR'
        ]
        new Scramble(scrambleMoves: scramble, expectedEdges: expectedEdges, expectedCorners: expectedCorners)
    }


    static def sampleScrambledCube2() {
        String scramble = "L F2 R U2 B2 R D2 R2 F2 U2 R U' B' F D2 L R2 D' F' R"
        def expectedEdges = [
                (UF): 'GR',
                (UL): 'GW',
                (UB): 'RW',
                (UR): 'BY',

                (FL): 'BO',
                (FR): 'RY',
                (BL): 'BR',
                (BR): 'YO',

                (DF): 'GY',
                (DR): 'OG',
                (DB): 'BW',
                (DL): 'OW'
        ]
        def expectedCorners = [
                (UFR): 'WRB',
                (UFL): 'BYR',
                (UBL): 'RGY',
                (UBR): 'YOG',
                (DFL): 'BOW',
                (DFR): 'GOW',
                (DBR): 'YOB',
                (DBL): 'GWR'
        ]
        new Scramble(scrambleMoves: scramble, expectedEdges: expectedEdges, expectedCorners: expectedCorners)
    }
}
