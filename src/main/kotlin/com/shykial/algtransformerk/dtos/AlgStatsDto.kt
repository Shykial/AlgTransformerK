package com.shykial.algtransformerk.dtos

class AlgStatsDto(
    val rotationlessAlgorithm: String,
    val rawAlgorithm: String,
    val rotationCount: Int,
    val stmBeforeCancellations: Int,
    val stmAfterCancellations: Int,
    val movesCancelled: Int,
    val initialCubeState: CubeStateDto,
    val postAlgCubeState: CubeStateDto
)
