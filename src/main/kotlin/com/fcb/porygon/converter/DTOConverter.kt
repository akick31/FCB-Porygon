package com.fcb.porygon.converter

import com.fcb.porygon.domain.NewSignup
import com.fcb.porygon.domain.User
import com.fcb.porygon.models.dto.NewSignupDTO
import com.fcb.porygon.models.dto.UserDTO
import org.springframework.stereotype.Component

@Component
class DTOConverter {
    fun convertToUserDTO(user: User): UserDTO {
        return UserDTO(
            id = user.id,
            username = user.username,
            coachName = user.coachName,
            discordTag = user.discordTag,
            discordId = user.discordId,
            role = user.role,
            team = user.team,
            delayOfGameInstances = user.delayOfGameInstances,
            wins = user.wins,
            losses = user.losses,
            winPercentage = user.winPercentage,
            conferenceWins = user.conferenceWins,
            conferenceLosses = user.conferenceLosses,
            conferenceWinPercentage = user.conferenceWinPercentage,
            seriesWins = user.seriesWins,
            seriesLosses = user.seriesLosses,
            seriesPushes = user.seriesPushes,
            conferenceChampionships = user.conferenceChampionships,
            tournamentAppearances = user.tournamentAppearances,
            superRegionalAppearances = user.superRegionalAppearances,
            collegeWorldSeriesAppearances = user.collegeWorldSeriesAppearances,
            championships = user.championships,
            averageResponseTime = user.averageResponseTime,
        )
    }

    fun convertToNewSignupDTO(newSignup: NewSignup): NewSignupDTO {
        return NewSignupDTO(
            id = newSignup.id,
            username = newSignup.username,
            coachName = newSignup.coachName,
            discordTag = newSignup.discordTag,
            discordId = newSignup.discordId,
            teamChoiceOne = newSignup.teamChoiceOne,
            teamChoiceTwo = newSignup.teamChoiceTwo,
            teamChoiceThree = newSignup.teamChoiceThree,
            approved = newSignup.approved,
        )
    }
}
