package com.shykial.algtransformerk.model

import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class AlgStats(
    val rawAlgorithm: String,
    val standardizedAlgorithm: String,
    val stmBeforeCancellations: Int,
    val stmAfterCancellations: Int,
    @ManyToOne
    @Cascade(CascadeType.PERSIST, CascadeType.REFRESH)
    @JoinColumn(name = "post_alg_cube_state_ID")
    val postAlgCubeState: CubeState,

    @ManyToOne
    @Cascade(CascadeType.PERSIST, CascadeType.REFRESH)
    @JoinColumn(name = "initial_cube_state_ID")
    val initialCubeState: CubeState
) : BaseEntity()