package com.fcb.porygon.models.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fcb.porygon.domain.Game.Subdivision
import com.fcb.porygon.domain.Game.GameType

data class StartRequest(
    @JsonProperty("subdivision") val subdivision: Subdivision,
    @JsonProperty("homeTeam") val homeTeam: String,
    @JsonProperty("awayTeam") val awayTeam: String,
    @JsonProperty("gameType") val gameType: GameType,
)
