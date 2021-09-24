package com.shykial.algtransformerk.model

import javax.persistence.Entity

@Entity
class Corner(
    val cornerPosition: CornerPosition,
    val currentPieceColors: String,
    val cornerPieceState: CornerPieceState,
) : BaseEntity() {
    val solved: Boolean
        get() = cornerPieceState == CornerPieceState.SOLVED
}

@Entity
class Edge(
    val currentPieceColors: String,
    val edgePosition: EdgePosition,
    val edgePieceState: EdgePieceState
) : BaseEntity() {
    val solved: Boolean
        get() = edgePieceState == EdgePieceState.SOLVED
}

enum class CornerPosition(val solvedState: String) {
    // U face corners:
    UBL("WBO"),
    UBR("WBR"),
    UFR("WGR"),
    UFL("WGO"),

    // D face corners:
    DBL("YBO"),
    DBR("YBR"),
    DFR("YGR"),
    DFL("YGO")
}

enum class EdgePosition(val solvedState: String) {
    // U face edges:
    UB("WB"),
    UR("WR"),
    UF("WG"),
    UL("WO"),

    // E slice edges:
    BL("BO"),
    BR("BR"),
    FL("GO"),
    FR("GR"),

    // D face edges:
    DB("YB"),
    DR("YR"),
    DF("YG"),
    DL("YO");
}