package com.shykial.algtransformerk.helpers

import com.shykial.algtransformerk.services.SIGN_TO_MOVE_FLAG
import com.shykial.algtransformerk.services.WHITE_SPACE_REGEX

private const val MIDDLE_MOVES = "MSE"
private val MOVE_FLAG_TO_SIGN = mapOf(
    1 to "",
    2 to "2",
    3 to "'"
)
private val DEFAULT_MOVE_MAP = "FBRLUD".associateWith { it }
private val AXIS_MOVES = mapOf(
    RotationAxis.X to "UFDB",
    RotationAxis.Y to "FRBL",
    RotationAxis.Z to "ULDR"
)

enum class RotationAxis {
    X, Y, Z
}

class MutableRotation(val state: MutableMap<Char, Char> = DEFAULT_MOVE_MAP.toMutableMap()) {
    constructor(initialRotation: String) : this() {
        initialRotation.split(WHITE_SPACE_REGEX).forEach(this::addRotation)
    }

    fun addRotation(rotation: String) {
        val rotationAxis = RotationAxis.valueOf(rotation.first().uppercase())
        val rotationFlag = SIGN_TO_MOVE_FLAG.getValue(rotation.last())
        val axisMoves = AXIS_MOVES[rotationAxis]!!.toList()
        val affectedMoves = axisMoves.map { state[it]!! }

        axisMoves.forEachIndexed { index, move ->
            state[move] = affectedMoves[(index + rotationFlag).mod(4)]
        }
    }

    fun affectMove(move: String) = move.replaceFirstChar {
        state[it] ?: throw IllegalArgumentException("Cannot affect provided move $move")
    }
}

fun withoutRotations(moves: List<String>, initialRotation: MutableRotation = MutableRotation()) =
    moves.flatMap { transformMoveAffectingRotation(it, initialRotation) }

fun transformMoveAffectingRotation(move: String, currentRotation: MutableRotation): List<String> {
    if (move.first().uppercase() in RotationAxis.values().map(RotationAxis::name)) {
        currentRotation.addRotation(move)
        return emptyList()
    }
    if (move.first() in MIDDLE_MOVES || move.first().isLowerCase() || 'w' in move) {
        val (newMoves, effectiveRotation) = toMovesAndRotation(move)
        currentRotation.addRotation(effectiveRotation)
        return newMoves.map(currentRotation::affectMove)
    }
    return listOf(currentRotation.affectMove(move))
}

private fun toMovesAndRotation(wideOrMiddleMove: String): Pair<List<String>, String> {
    val moveFlag = SIGN_TO_MOVE_FLAG.getValue(wideOrMiddleMove.last())
    fun getSign(opposite: Boolean = false) = MOVE_FLAG_TO_SIGN[if (opposite) (-moveFlag).mod(4) else moveFlag]

    return when (wideOrMiddleMove.first().uppercaseChar()) {
        'R' -> listOf("L${getSign()}") to "x${getSign()}"
        'L' -> listOf("R${getSign()}") to "x${getSign(true)}"
        'F' -> listOf("B${getSign()}") to "z${getSign()}"
        'B' -> listOf("F${getSign()}") to "z${getSign(true)}"
        'U' -> listOf("D${getSign()}") to "y${getSign()}"
        'D' -> listOf("U${getSign()}") to "y${getSign(true)}"

        'M' -> listOf("R${getSign()}", "L${getSign(true)}") to "x${getSign(true)}"
        'S' -> listOf("F${getSign(true)}", "B${getSign()}") to "z${getSign()}"
        'E' -> listOf("U${getSign()}", "D${getSign(true)}") to "y${getSign(true)}"
        else -> throw IllegalArgumentException()
    }
}
