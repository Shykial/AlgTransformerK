package com.shykial.algtransformerk.repositories

import com.shykial.algtransformerk.model.CubeState
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CubeStateRepository : JpaRepository<CubeState, UUID> {

    @Query("select c from CubeState c where c.leadingMoves = :leadingMoves")
    fun findByLeadingMoves(@Param("leadingMoves") leadingMoves: String): CubeState?
}