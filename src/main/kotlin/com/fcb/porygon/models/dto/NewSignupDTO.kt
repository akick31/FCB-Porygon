package com.fcb.porygon.models.dto

data class NewSignupDTO(
    val id: Long,
    var username: String,
    var coachName: String,
    var discordTag: String,
    var discordId: String,
    var teamChoiceOne: String,
    var teamChoiceTwo: String,
    var teamChoiceThree: String,
    var approved: Boolean,
)
