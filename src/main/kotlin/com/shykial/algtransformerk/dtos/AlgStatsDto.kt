package com.shykial.algtransformerk.dtos

class AlgStatsDto(
    val rawAlgorithm: String,
    val standardizedAlgorithm: String,
    val stmBeforeCancellations: Int,
    val stmAfterCancellations: Int,
    val movesCancelled: Int,
    val postAlgCubeState: CubeStateDto,
    val initialCubeState: CubeStateDto
)
