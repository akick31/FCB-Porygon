package com.fcb.porygon.models.game

import com.fcb.porygon.domain.Game.ActualResult
import com.fcb.porygon.domain.Game.InningHalf

data class GameScenario(
    val result: ActualResult,
    val batterOnFirst: Boolean,
    val batterOnSecond: Boolean,
    val batterOnThird: Boolean,
    val runsScored: Int,
    val inning: Int,
    val inningHalf: InningHalf,
    val outs: Int
)