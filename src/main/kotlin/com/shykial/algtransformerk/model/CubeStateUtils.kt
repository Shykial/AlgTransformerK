package com.shykial.algtransformerk.model

import java.util.*

private val CORNER_STRINGS = mapOf(
    CornerPieceState.SOLVED to "",
    CornerPieceState.MISPLACED to "c",
    CornerPieceState.TWISTED to "tc"
)

private val EDGE_STRINGS = mapOf(
    EdgePieceState.SOLVED to "",
    EdgePieceState.MISPLACED to "e",
    EdgePieceState.FLIPPED to "fe"
)

fun CubeState.getGroupedCorners(): Map<CornerPieceState, Map<CornerPosition, String>> =
    corners.groupingBy { it.cornerPieceState }
        .aggregate { _, accumulator: EnumMap<CornerPosition, String>?, element, first ->
            val innerMap = if (first) EnumMap(CornerPosition::class.java) else accumulator!!
            innerMap.apply { this[element.cornerPosition] = element.currentPieceColors }
        }


fun CubeState.getGroupedEdges(): Map<EdgePieceState, Map<EdgePosition, String>> =
    edges.groupingBy { it.edgePieceState }
        .aggregate { _, accumulator: EnumMap<EdgePosition, String>?, element, first ->
            val innerMap = if (first) EnumMap(EdgePosition::class.java) else accumulator!!
            innerMap.apply { this[element.edgePosition] = element.currentPieceColors }
        }

fun CubeState.cubeStateString(): String = edgesStateString() + cornersStateString()

private fun CubeState.cornersStateString(): String =
    corners.filterNot(Corner::solved)
        .groupingBy(Corner::cornerPieceState)
        .eachCount()
        .toSortedMap()
        .entries.fold(StringBuilder()) { s, (state, count) ->
            s.append("$count${CORNER_STRINGS[state]}")
        }.toString()


private fun CubeState.edgesStateString(): String =
    edges.filterNot(Edge::solved)
        .groupingBy(Edge::edgePieceState)
        .eachCount()
        .toSortedMap()
        .entries.fold(StringBuilder()) { s, (state, count) ->
            s.append("$count${EDGE_STRINGS[state]}")
        }.toString()





