package com.shykial.algtransformerk

import com.shykial.algtransformerk.dtos.AlgStatsDto
import com.shykial.algtransformerk.dtos.CubeStateDto
import com.shykial.algtransformerk.dtos.SimpleStandardizedAlg
import com.shykial.algtransformerk.model.*

fun AlgStats.toDto() = AlgStatsDto(
    rawAlgorithm = rawAlgorithm,
    standardizedAlgorithm = standardizedAlgorithm,
    movesCancelled = stmBeforeCancellations - stmAfterCancellations,
    rotationCount = rotationCount,
    stmBeforeCancellations = stmBeforeCancellations,
    stmAfterCancellations = stmAfterCancellations,
    initialCubeState = initialCubeState.toDto(),
    postAlgCubeState = postAlgCubeState.toDto()
)

fun CubeState.toDto() = CubeStateDto(
    cubeStateString = cubeStateString(),
    corners = getGroupedCorners(),
    edges = getGroupedEdges(),
    solvedEdges = edges.count(Edge::solved),
    flippedEdges = edges.count { it.edgePieceState == EdgePieceState.FLIPPED },
    misplacedEdges = edges.count { it.edgePieceState == EdgePieceState.MISPLACED },
    solvedCorners = corners.count(Corner::solved),
    twistedCorners = corners.count { it.cornerPieceState == CornerPieceState.TWISTED },
    misplacedCorners = corners.count { it.cornerPieceState == CornerPieceState.MISPLACED }
)

fun AlgStats.toSimpleDto() = SimpleStandardizedAlg(
    rawAlgorithm = rawAlgorithm,
    standardizedAlgorithm = standardizedAlgorithm,
    movesCancelled = stmBeforeCancellations - stmAfterCancellations,
    postAlgCubeState = postAlgCubeState.cubeStateString()
)