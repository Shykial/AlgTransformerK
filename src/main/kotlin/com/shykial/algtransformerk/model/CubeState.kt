package com.shykial.algtransformerk.model

import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import java.util.*
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToMany

private val CORNER_STRINGS = mapOf(
    CornerPieceState.SOLVED to "",
    CornerPieceState.MISPLACED to "c",
    CornerPieceState.TWISTED to "tc"
)

private val EDGE_STRINGS = mapOf(
    EdgePieceState.SOLVED to "",
    EdgePieceState.MISPLACED to "e",
    EdgePieceState.FLIPPED to "fe"
)

@Entity
class CubeState(
    val leadingMoves: String,

    @OneToMany
    @Cascade(CascadeType.PERSIST, CascadeType.REFRESH)
    @JoinColumn(name = "cube_state_ID")
    val corners: Set<Corner>,

    @OneToMany
    @Cascade(CascadeType.PERSIST, CascadeType.REFRESH)
    @JoinColumn(name = "cube_state_ID")
    val edges: Set<Edge>,
) : BaseEntity()

fun CubeState.getGroupedCorners(): Map<CornerPieceState, Map<CornerPosition, String>> =
    corners.groupingBy(Corner::cornerPieceState)
        .aggregate { _, accumulator: EnumMap<CornerPosition, String>?, element, first ->
            val innerMap = if (first) EnumMap(CornerPosition::class.java) else accumulator!!
            innerMap.apply { this[element.cornerPosition] = element.currentPieceColors }
        }

fun CubeState.getGroupedEdges(): Map<EdgePieceState, Map<EdgePosition, String>> =
    edges.groupingBy(Edge::edgePieceState)
        .aggregate { _, accumulator: EnumMap<EdgePosition, String>?, element, first ->
            val innerMap = if (first) EnumMap(EdgePosition::class.java) else accumulator!!
            innerMap.apply { this[element.edgePosition] = element.currentPieceColors }
        }

fun CubeState.cubeStateString(): String = edgesStateString() + cornersStateString()

private fun CubeState.cornersStateString(): String =
    corners.filterNot(Corner::solved)
        .groupingBy(Corner::cornerPieceState)
        .eachCount()
        .toSortedMap()
        .entries.joinToString { (state, count) -> "$count${CORNER_STRINGS[state]}" }

private fun CubeState.edgesStateString(): String =
    edges.filterNot(Edge::solved)
        .groupingBy(Edge::edgePieceState)
        .eachCount()
        .toSortedMap()
        .entries.joinToString { (state, count) -> "$count${EDGE_STRINGS[state]}" }