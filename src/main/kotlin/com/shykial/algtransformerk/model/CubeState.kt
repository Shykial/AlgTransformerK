package com.shykial.algtransformerk.model

import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToMany

@Entity
class CubeState(
    @OneToMany
    @JoinColumn(name = "cube_state_ID")
    val corners: Set<Corner>,

    @OneToMany
    @JoinColumn(name = "cube_state_ID")
    val edges: Set<Edge>,
) : BaseEntity()

