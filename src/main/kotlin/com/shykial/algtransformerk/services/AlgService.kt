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

private val INNER_MOVES_PATTERN = Regex("""[\[(] (.*?) [)\]] (?:\s?\*?\s?(\d))?""", option = RegexOption.COMMENTS)

@Service
class AlgService(val algStatsRepository: AlgStatsRepository) {

    fun getStatsForAlgorithm(rawAlgorithm: String): AlgStatsDto {
        val algStats = algStatsRepository.findByRawAlgorithm(rawAlgorithm) ?: generateAlgStats(rawAlgorithm)
        return algStats.toDto()
    }

    private fun generateAlgStats(rawAlgorithm: String): AlgStats {
        val moves = readMoves(rawAlgorithm)
        TODO()
    }

    private fun readMoves(rawAlgorithm: String): List<String> {
        Regex("""[\[(] (.*?) [)\]] (?:\s?\*?\s?(\d))?""", option = RegexOption.COMMENTS)
            .findAll(rawAlgorithm)
            .map {
                it.groupValues.filter(String::isNotEmpty)
                it.range to it.groupValues
            }
            .toList()
        TODO()
    }
}
