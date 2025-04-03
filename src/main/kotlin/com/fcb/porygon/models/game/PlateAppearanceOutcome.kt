package com.fcb.porygon.models.game

import com.fcb.porygon.domain.Game.ActualResult
import com.fcb.porygon.domain.Game.BaseCondition
import com.fcb.porygon.domain.Player

data class PlateAppearanceOutcome(
    val actualResult: ActualResult,
    val outs: Int,
    val runsScored: Int,
    val homeScore: Int,
    val awayScore: Int,
    val runnerOnFirstAfter: Player?,
    val runnerOnSecondAfter: Player?,
    val runnerOnThirdAfter: Player?,
    val baseConditionAfter: BaseCondition,
)
