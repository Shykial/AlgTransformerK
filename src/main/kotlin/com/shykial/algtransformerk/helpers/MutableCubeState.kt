package com.shykial.algtransformerk.helpers

import com.shykial.algtransformerk.model.CornerPieceState
import com.shykial.algtransformerk.model.CornerPosition
import com.shykial.algtransformerk.model.CornerPosition.*
import com.shykial.algtransformerk.model.EdgePieceState
import com.shykial.algtransformerk.model.EdgePosition
import com.shykial.algtransformerk.model.EdgePosition.*
import com.shykial.algtransformerk.services.MOVE_FLAG_REGEX
import com.shykial.algtransformerk.services.SIGN_TO_MOVE_FLAG
import java.util.*

private val CORNERS = EnumMap(CornerPosition.values().associateWith { it.solvedState })
private val EDGES = EnumMap(EdgePosition.values().associateWith { it.solvedState })

fun String.swapWithNext(firstIndex: Int): String {
    val secondIndex = (firstIndex + 1).mod(this.length)
    return String(toCharArray().apply {
        this[firstIndex] = this@swapWithNext[secondIndex]
        this[secondIndex] = this@swapWithNext[firstIndex]
    })
}

class MutableCubeState(
    val leadingMoves: MutableList<String> = mutableListOf(),
    val corners: MutableMap<CornerPosition, String> = CORNERS.clone(),
    val edges: MutableMap<EdgePosition, String> = EDGES.clone()
) {
    constructor(moves: List<String>) : this() {
        moves.forEach(this::makeMove)
    }

    fun makeMove(move: String) {
        leadingMoves.add(move)
        val moveAxis = move.replace(MOVE_FLAG_REGEX, "")
        val moveFlag = SIGN_TO_MOVE_FLAG.getValue(move.last())

        val (affectedEdges, affectedCorners) =
            when (moveAxis) {
                "R" -> (arrayOf(UR, BR, DR, FR) to null) to (arrayOf(UFR, UBR, DBR, DFR) to 0)
                "L" -> (arrayOf(UL, FL, DL, BL) to null) to (arrayOf(UFL, DFL, DBL, UBL) to 0)
                "U" -> (arrayOf(UF, UL, UB, UR) to null) to (arrayOf(UFR, UFL, UBL, UBR) to 1)
                "D" -> (arrayOf(DF, DR, DB, DL) to null) to (arrayOf(DFR, DBR, DBL, DFL) to 1)
                "F" -> (arrayOf(UF, FR, DF, FL) to 0) to (arrayOf(UFL, UFR, DFR, DFL) to 2)
                "B" -> (arrayOf(UB, BL, DB, BR) to 0) to (arrayOf(UBL, DBL, DBR, UBR) to 2)
                else -> throw IllegalArgumentException("Not supported move axis: $moveAxis")
            }

        affectPieces(corners, affectedCorners.first, affectedCorners.second, moveFlag)
        affectPieces(edges, affectedEdges.first, affectedEdges.second, moveFlag)
    }

    fun cornerState(position: CornerPosition) = when {
        corners[position] == position.solvedState -> CornerPieceState.SOLVED
        corners[position]?.toHashSet() == position.solvedState.toHashSet() -> CornerPieceState.TWISTED
        else -> CornerPieceState.MISPLACED
    }

    fun edgeState(position: EdgePosition) = when {
        edges[position] == position.solvedState -> EdgePieceState.SOLVED
        edges[position]?.toHashSet() == position.solvedState.toHashSet() -> EdgePieceState.FLIPPED
        else -> EdgePieceState.MISPLACED
    }
}

private inline fun <reified T> affectPieces(
    pieceMap: MutableMap<T, String>,
    affectedPieces: Array<T>,
    firstSwappedIndex: Int?,
    moveFlag: Int
) {
    val initialValues = affectedPieces.map {
        pieceMap[it] ?: throw IllegalArgumentException("${T::class.simpleName} map not initialized properly")
    }

    initialValues.forEachIndexed { index, value ->
        val selectedPiece = affectedPieces[(index + moveFlag).mod(affectedPieces.size)]
        pieceMap[selectedPiece] = if (moveFlag != 2 && firstSwappedIndex != null)
            value.swapWithNext(firstSwappedIndex) else value
    }
}
