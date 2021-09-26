package com.shykial.algtransformerk.services

import com.shykial.algtransformerk.dtos.AlgStatsDto
import com.shykial.algtransformerk.model.AlgStats
import com.shykial.algtransformerk.repositories.AlgStatsRepository
import com.shykial.algtransformerk.toDto
import org.springframework.stereotype.Service

private val OPPOSITE_MOVES = mapOf(
    'R' to 'L',
    'L' to 'R',
    'U' to 'D',
    'D' to 'U',
    'F' to 'B',
    'B' to 'F'
)

private val MOVE_FLAGS = mapOf(
    '\'' to -1,
    '2' to 2
).withDefault { 1 }

private val OPPOSITE_MOVE_FLAGS = mapOf(
    '\'' to "",
    '2' to "2"
).withDefault { "'" }

private val INNER_MOVES_REGEX = Regex("""[\[(] (.*?) [)\]] (?:\s?\*?\s?(\d))?""", option = RegexOption.COMMENTS)
private val WHITE_SPACE_REGEX = Regex("""\s+""")
private val MOVE_FLAG_REGEX = Regex("""['2]$""")

operator fun <T> List<T>.times(times: Int): List<T> = (1..times).flatMap { this }

@Service
class AlgService(private val algStatsRepository: AlgStatsRepository) {
    fun getStatsForAlgorithm(rawAlgorithm: String): AlgStatsDto {
        val algStats = algStatsRepository.findByRawAlgorithm(rawAlgorithm) ?: generateAlgStats(rawAlgorithm)
        return algStats.toDto()
    }

    private fun generateAlgStats(rawAlgorithm: String): AlgStats {
        val moves = withCancellations(readMoves(rawAlgorithm))
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

private fun withCancellations(inputList: List<String>): List<String> {
    val effectiveList = inputList.toMutableList()
    var currentIndex = 1
    while (currentIndex <= effectiveList.lastIndex) {
        if (effectiveList[currentIndex].first() == effectiveList[currentIndex - 1].first()) {
            currentIndex = effectiveList.cancelReturningIndex(currentIndex - 1, currentIndex)
        } else {
            var j = currentIndex
            val oppositeMoveChar = OPPOSITE_MOVES[effectiveList[currentIndex - 1].first()]
            while (j < effectiveList.size && effectiveList[j].first() == oppositeMoveChar) j++
            if (effectiveList[currentIndex - 1].first() == effectiveList[j].first())
                currentIndex = effectiveList.cancelReturningIndex(currentIndex - 1, j)
        }
        currentIndex++
    }
    return effectiveList
}

private fun MutableList<String>.cancelReturningIndex(firstIndex: Int, secondIndex: Int): Int {
    val newMove = cancelMoves(this[firstIndex], this[secondIndex])
    removeAt(secondIndex)
    if (newMove == null) {
        removeAt(firstIndex)
        return firstIndex - 1
    }
    set(firstIndex, newMove)
    return firstIndex
}

private fun cancelMoves(firstMove: String, secondMove: String): String? {
    val withoutFlag = firstMove.replace(MOVE_FLAG_REGEX, "")
    return when (MOVE_FLAGS.getValue(firstMove.last()) + MOVE_FLAGS.getValue(secondMove.last())) {
        -1, 3 -> "$withoutFlag'"
        0, 4 -> null
        1 -> withoutFlag
        2, -2 -> "${withoutFlag}2"
        else -> throw IllegalArgumentException()
    }
}