package com.shykial.algtransformerk.dtos

import com.shykial.algtransformerk.model.CornerPieceState
import com.shykial.algtransformerk.model.CornerPosition
import com.shykial.algtransformerk.model.EdgePieceState
import com.shykial.algtransformerk.model.EdgePosition

class CubeStateDto(
    val cubeStateString: String,
    val corners: Map<CornerPieceState, Map<CornerPosition, String>>,
    val edges: Map<EdgePieceState, Map<EdgePosition, String>>,
    val solvedEdges: Int,
    val misplacedEdges: Int,
    val flippedEdges: Int,
    val solvedCorners: Int,
    val misplacedCorners: Int,
    val twistedCorners: Int
)
