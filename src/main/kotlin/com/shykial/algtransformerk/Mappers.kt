package com.shykial.algtransformerk

import com.shykial.algtransformerk.dtos.AlgStatsDto
import com.shykial.algtransformerk.dtos.CubeStateDto
import com.shykial.algtransformerk.helpers.MutableCubeState
import com.shykial.algtransformerk.model.*

fun AlgStats.toDto() = AlgStatsDto(
    rawAlgorithm = rawAlgorithm,
    standardizedAlgorithm = standardizedAlgorithm,
    movesCancelled = stmBeforeCancellations - stmAfterCancellations,
    stmBeforeCancellations = stmBeforeCancellations,
    stmAfterCancellations = stmAfterCancellations,
    postAlgCubeState = postAlgCubeState.toDto(),
    initialCubeState = initialCubeState.toDto()
)

fun CubeState.toDto() = CubeStateDto(
    cubeStateString = cubeStateString,
    corners = groupedCorners,
    edges = groupedEdges,
    solvedEdges = edges.count(Edge::solved),
    flippedEdges = edges.count { it.edgePieceState == EdgePieceState.FLIPPED },
    misplacedEdges = edges.count { it.edgePieceState == EdgePieceState.MISPLACED },
    solvedCorners = corners.count(Corner::solved),
    twistedCorners = corners.count { it.cornerPieceState == CornerPieceState.TWISTED },
    misplacedCorners = corners.count { it.cornerPieceState == CornerPieceState.MISPLACED }
)

fun MutableCubeState.toCubeState() = CubeState(
    leadingMoves = leadingMoves.joinToString(" "),
    corners = corners.asSequence().map { (position, value) ->
        Corner(position, value, cornerState(position))
    }.toSet(),
    edges = edges.asSequence().map { (position, value) ->
        Edge(position, value, edgeState(position))
    }.toSet()
)