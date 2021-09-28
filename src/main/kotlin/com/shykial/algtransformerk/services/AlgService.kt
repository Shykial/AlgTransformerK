package com.shykial.algtransformerk.services

import com.shykial.algtransformerk.dtos.AlgStatsDto
import com.shykial.algtransformerk.helpers.withoutRotations
import com.shykial.algtransformerk.model.AlgStats
import com.shykial.algtransformerk.repositories.AlgStatsRepository
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

private val OPPOSITE_MOVE_FLAGS = mapOf(
    '\'' to "",
    '2' to "2"
).withDefault { "'" }

private val INNER_MOVES_REGEX = Regex("""[\[(] (.*?) [)\]] (?:\s?\*?\s?(\d))?""", option = RegexOption.COMMENTS)
val WHITE_SPACE_REGEX = Regex("""\s+""")
val MOVE_FLAG_REGEX = Regex("""['2]$""")

operator fun <T> List<T>.times(times: Int): List<T> = (1..times).flatMap { this }

@Service
class AlgService(private val algStatsRepository: AlgStatsRepository) {
    fun getStatsForAlgorithm(rawAlgorithm: String): AlgStatsDto {
        val algStats = algStatsRepository.findByRawAlgorithm(rawAlgorithm) ?: generateAlgStats(rawAlgorithm)
        return algStats.toDto()
    }

    private fun generateAlgStats(rawAlgorithm: String): AlgStats {
        val moves = readMoves(rawAlgorithm)
        val standardizedMoves = withoutRotations(moves)
        val movesWithCancellations = withCancellations(standardizedMoves)
        TODO()
    }
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
    if (match.groupValues.size >= 3 && match.groupValues[2].isNotBlank()) moves = moves * match.groupValues[2].toInt()
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
    val summedFlags = SIGN_TO_MOVE_FLAG.getValue(firstMove.last()) + SIGN_TO_MOVE_FLAG.getValue(secondMove.last())
    return when (summedFlags.mod(4)) {
        0 -> null
        1 -> withoutFlag
        2 -> "${withoutFlag}2"
        3 -> "$withoutFlag'"
        else -> throw IllegalArgumentException()
    }
}