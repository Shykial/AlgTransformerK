package com.shykial.algtransformerk.repositories

import com.shykial.algtransformerk.model.AlgStats
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AlgStatsRepository : JpaRepository<AlgStats, UUID> {

}
