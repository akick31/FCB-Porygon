package com.fcb.porygon.models.response

import com.fcb.porygon.domain.Game

data class ScorebugResponse(
    val gameId: Int,
    val scorebug: ByteArray?,
    val homeTeam: String,
    val awayTeam: String,
    val status: Game.GameStatus?,
)
