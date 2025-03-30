package com.fcb.porygon.models.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fcb.porygon.domain.Game.GameType
import com.fcb.porygon.domain.Game.Subdivision

data class StartRequest(
    @JsonProperty("subdivision") val subdivision: Subdivision,
    @JsonProperty("homeTeam") val homeTeam: String,
    @JsonProperty("awayTeam") val awayTeam: String,
    @JsonProperty("gameType") val gameType: GameType,
    @JsonProperty("seriesGameNumber") val seriesGameNumber: Int,
)
