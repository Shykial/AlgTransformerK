package com.shykial.algtransformerk.controllers

import com.shykial.algtransformerk.dtos.AlgStatsDto
import com.shykial.algtransformerk.dtos.SimpleStandardizedAlg
import com.shykial.algtransformerk.services.AlgService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AlgTransformerController(val algService: AlgService) {

    @GetMapping("/alg")
    fun statsForAlgorithm(@RequestParam("algorithm") rawAlgorithm: String): AlgStatsDto =
        algService.getStatsForAlgorithm(rawAlgorithm)

    @GetMapping("/simple")
    fun standardizedWithCancellations(@RequestParam("algorithm") rawAlgorithm: String): SimpleStandardizedAlg =
        algService.getSimpleStandardizedAlg(rawAlgorithm)
}