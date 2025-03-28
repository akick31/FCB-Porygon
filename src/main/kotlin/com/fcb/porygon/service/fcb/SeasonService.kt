package com.fcb.porygon.service.fcfb

import com.fcb.porygon.domain.Game
import com.fcb.porygon.domain.Season
import com.fcb.porygon.repositories.SeasonRepository
import com.fcb.porygon.utils.CurrentSeasonNotFoundException
import com.fcb.porygon.utils.CurrentWeekNotFoundException
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Component
class SeasonService(
    private val seasonRepository: SeasonRepository,
) {
    /**
     * Start the current season
     */
    fun startSeason() {
        val previousSeason = seasonRepository.getPreviousSeason()
        val season =
            Season(
                seasonNumber = previousSeason?.seasonNumber?.plus(1) ?: 1,
                startDate = ZonedDateTime.now(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")),
                endDate = null,
                nationalChampionshipWinningTeam = null,
                nationalChampionshipLosingTeam = null,
                nationalChampionshipWinningCoach = null,
                nationalChampionshipLosingCoach = null,
                currentWeek = 1,
                currentSeason = true,
            )
        seasonRepository.save(season)
    }

    /**
     * End the current season
     * @param game the national championship game
     */
    fun endSeason(game: Game) {
        val season = getCurrentSeason()
        season.currentSeason = false

        if (game.homeScore > game.awayScore) {
            season.nationalChampionshipWinningTeam = game.homeTeam
            season.nationalChampionshipLosingTeam = game.awayTeam
            season.nationalChampionshipWinningCoach = game.homeCoach
            season.nationalChampionshipLosingCoach = game.awayCoach
        } else {
            season.nationalChampionshipWinningTeam = game.awayTeam
            season.nationalChampionshipLosingTeam = game.homeTeam
            season.nationalChampionshipWinningCoach = game.awayCoach
            season.nationalChampionshipLosingCoach = game.homeCoach
        }

        val now = ZonedDateTime.now(ZoneId.of("America/New_York"))
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
        season.endDate = now.format(formatter)

        seasonRepository.save(season)
    }

    /**
     * Get the current season
     */
    fun getCurrentSeason() = seasonRepository.getCurrentSeason() ?: throw CurrentSeasonNotFoundException()

    /**
     * Get the current week
     */
    fun getCurrentWeek() = seasonRepository.getCurrentSeason()?.currentWeek ?: throw CurrentWeekNotFoundException()

    /**
     * Increment the current week
     */
    fun incrementWeek() {
        val season = getCurrentSeason()
        season.currentWeek = season.currentWeek.plus(1)
        seasonRepository.save(season)
    }
}
