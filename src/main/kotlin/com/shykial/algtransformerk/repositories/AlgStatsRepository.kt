package com.shykial.algtransformerk.repositories

import com.shykial.algtransformerk.model.AlgStats
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AlgStatsRepository : JpaRepository<AlgStats, UUID> {

    @Query("select a from AlgStats a where a.rawAlgorithm = :rawAlgorithm")
    fun findByRawAlgorithm(@Param("rawAlgorithm") rawAlgorithm: String): AlgStats?

    @Query("select a from AlgStats a where a.standardizedAlgorithm = :algorithm")
    fun findByStandardizedAlgorithm(@Param("algorithm") algorithm: String): AlgStats?
}
