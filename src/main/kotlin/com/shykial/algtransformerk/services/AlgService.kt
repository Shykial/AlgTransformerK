package com.shykial.algtransformerk.services

import com.shykial.algtransformerk.dtos.AlgStatsDto
import com.shykial.algtransformerk.dtos.SimpleStandardizedAlg
import com.shykial.algtransformerk.helpers.MIDDLE_MOVES
import com.shykial.algtransformerk.helpers.MutableCubeState
import com.shykial.algtransformerk.helpers.RotationAxis
import com.shykial.algtransformerk.helpers.withoutRotations
import com.shykial.algtransformerk.model.AlgStats
import com.shykial.algtransformerk.model.CubeState
import com.shykial.algtransformerk.model.cubeStateString
import com.shykial.algtransformerk.repositories.AlgStatsRepository
import com.shykial.algtransformerk.repositories.CubeStateRepository
import com.shykial.algtransformerk.toCubeState
import com.shykial.algtransformerk.toDto
import org.springframework.stereotype.Service

val OPPOSITE_MOVES = mapOf(
    'R' to 'L',
    'L' to 'R',
    'U' to 'D',
    'D' to 'U',
    'F' to 'B',
    'B' to 'F'
)

val SIGN_TO_MOVE_FLAG = mapOf(
    '\'' to 3,
    '2' to 2
).withDefault { 1 }

val WHITE_SPACE_REGEX = Regex("""\s+""")
val MOVE_FLAG_REGEX = Regex("""['2]$""")
val INNER_MOVES_REGEX = Regex("""[\[(] (.*?) [)\]] (?:\s?\*?\s?(\d))?""", option = RegexOption.COMMENTS)
private val EMPTY_LIST = emptyList<String>()
operator fun <T> List<T>.times(times: Int): List<T> = (1..times).flatMap { this }

@Service
class AlgService(
    private val algStatsRepository: AlgStatsRepository,
    private val cubeStateRepository: CubeStateRepository
) {
    fun getStatsForAlgorithm(rawAlgorithm: String): AlgStatsDto {
        val algStats = algStatsRepository.findByRawAlgorithm(rawAlgorithm) ?: generateAlgStats(rawAlgorithm)
        return algStats.toDto()
    }

    private fun generateAlgStats(rawAlgorithm: String): AlgStats {
        val standardizedMoves = standardizedMovesOf(rawAlgorithm)
        algStatsRepository.findByStandardizedAlgorithm(standardizedMoves.joinToString(" "))?.let { return it }

        val algStats = AlgStats(
            rawAlgorithm = rawAlgorithm,
            standardizedAlgorithm = standardizedMoves.joinToString(" "),
            stmBeforeCancellations = countRawAlgMoves(rawAlgorithm),
            stmAfterCancellations = standardizedMoves.size,
            initialCubeState = getOrCreateCubeState(),
            postAlgCubeState = getOrCreateCubeState(standardizedMoves)
        )
        return algStatsRepository.save(algStats)
    }

    private fun getOrCreateCubeState(leadingMoves: List<String> = EMPTY_LIST): CubeState =
        cubeStateRepository.findByLeadingMoves(leadingMoves.joinToString(" "))
            ?: cubeStateRepository.save(MutableCubeState(leadingMoves).toCubeState())

    fun getSimpleStandardizedAlg(rawAlgorithm: String): SimpleStandardizedAlg {
        return with(standardizedMovesOf(rawAlgorithm)) {
            val rawAlgMoveCount = countRawAlgMoves(rawAlgorithm)
            SimpleStandardizedAlg(
                standardizedAlgorithm = "${joinToString(" ")} (${size})",
                rawAlgorithm = "$rawAlgorithm ($rawAlgMoveCount)",
                movesCancelled = rawAlgMoveCount - size,
                postAlgCubeState = getOrCreateCubeState(this).cubeStateString()
            )
        }
    }
}

@Suppress("USELESS_CAST") /// KT-46360
private fun countRawAlgMoves(rawAlgorithm: String): Int =
    readMoves(rawAlgorithm).sumOf {
        when (it.first().uppercase()) {
            in RotationAxis.values().map(RotationAxis::name) -> 0
            in MIDDLE_MOVES -> 2
            else -> 1
        } as Int
    }

private fun standardizedMovesOf(algorithm: String): List<String> {
    val moves = readMoves(algorithm)
    val rotationlessMoves = withoutRotations(moves)
    return withCancellations(rotationlessMoves)
}

private fun readMoves(rawAlgorithm: String): List<String> {
    var currentIndex = 0
    return INNER_MOVES_REGEX
        .findAll(rawAlgorithm)
        .map(::readMovesFromMatch)
        .flatMap {
            val currentMoves =
                if (it.first.first > currentIndex)
                    rawAlgorithm.movesInRange(currentIndex until it.first.first) + it.second
                else it.second
            currentIndex = it.first.last + 1
            currentMoves
        }
        .toMutableList()
        .apply {
            if (currentIndex < rawAlgorithm.lastIndex)
                addAll(rawAlgorithm.movesInRange(currentIndex..rawAlgorithm.lastIndex))
        }
}

private fun readMovesFromMatch(match: MatchResult): Pair<IntRange, List<String>> {
    val innerString = match.groupValues[1].also {
        if ("[]()".any(it.substring(1 until it.lastIndex)::contains))
            throw NotImplementedError("Nested brackets not supported yet")
    }
    var moves = if ("," in innerString) expandedComm(innerString) else innerString.split(WHITE_SPACE_REGEX)
    if (match.groupValues.size >= 3 && match.groupValues[2].isNotBlank()) moves =
        moves * match.groupValues[2].toInt()
    return match.range to moves
}

private fun expandedComm(comm: String): List<String> {
    val (firstPart, secondPart) = comm.split(",").map { it.trim().split(WHITE_SPACE_REGEX) }
    return firstPart + secondPart +
            firstPart.asReversed().map(::toOppositeMove) +
            secondPart.asReversed().map(::toOppositeMove)
}

private fun toOppositeMove(move: String) =
    when (move.last()) {
        '\'' -> move.substring(0, move.lastIndex)
        '2' -> move
        else -> "$move'"
    }

private fun String.movesInRange(range: IntRange): List<String> =
    with(substring(range)) { if (isBlank()) emptyList() else trim().split(WHITE_SPACE_REGEX) }

private fun withCancellations(moves: List<String>): List<String> {
    val effectiveMoves = moves.toMutableList()
    var currentIndex = 1
    while (currentIndex <= effectiveMoves.lastIndex) {
        if (effectiveMoves[currentIndex].first() == effectiveMoves[currentIndex - 1].first()) {
            currentIndex = effectiveMoves.cancelReturningIndex(currentIndex - 1, currentIndex)
        } else {
            var j = currentIndex
            val oppositeMoveChar = OPPOSITE_MOVES[effectiveMoves[currentIndex - 1].first()]
            while (j < effectiveMoves.lastIndex && effectiveMoves[j].first() == oppositeMoveChar) j++
            if (effectiveMoves[currentIndex - 1].first() == effectiveMoves[j].first())
                currentIndex = effectiveMoves.cancelReturningIndex(currentIndex - 1, j)
        }
        currentIndex++
    }
    return effectiveMoves
}

private fun MutableList<String>.cancelReturningIndex(firstIndex: Int, secondIndex: Int): Int {
    val newMove = cancelMoves(this[firstIndex], this[secondIndex])
    removeAt(secondIndex)
    if (newMove == null) {
        removeAt(firstIndex)
        return if (firstIndex > 0) firstIndex - 1 else 0
    }
    set(firstIndex, newMove)
    return firstIndex
}

fun cancelMoves(firstMove: String, secondMove: String): String? {
    val withoutFlag = firstMove.replace(MOVE_FLAG_REGEX, "")
    val summedFlags = sequenceOf(firstMove, secondMove).sumOf { SIGN_TO_MOVE_FLAG.getValue(it.last()) }
    return when (summedFlags.mod(4)) {
        0 -> null
        1 -> withoutFlag
        2 -> "${withoutFlag}2"
        3 -> "$withoutFlag'"
        else -> throw IllegalArgumentException()
    }
}