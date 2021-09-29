package com.shykial.algtransformerk.model

import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class AlgStats(
    val rawAlgorithm: String,
    val standardizedAlgorithm: String,
    val rotationCount: Int,
    val stmBeforeCancellations: Int,
    val stmAfterCancellations: Int,
    @ManyToOne
    @JoinColumn(name = "initial_cube_state_ID")
    val initialCubeState: CubeState,
    @ManyToOne
    @JoinColumn(name = "post_alg_cube_state_ID")
    val postAlgCubeState: CubeState
) : BaseEntity()