package com.shykial.algtransformerk.dtos

class AlgStatsDto(
    val standardizedAlgorithm: String,
    val rawAlgorithm: String,
    val stmBeforeCancellations: Int,
    val stmAfterCancellations: Int,
    val movesCancelled: Int,
    val postAlgCubeState: CubeStateDto,
    val initialCubeState: CubeStateDto
)
