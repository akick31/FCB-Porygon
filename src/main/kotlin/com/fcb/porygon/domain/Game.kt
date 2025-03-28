package com.fcb.porygon.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fcb.porygon.domain.Game.InningHalf.TOP
import com.fcb.porygon.domain.Game.TeamSide.HOME
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "game", schema = "porygon")
class Game {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "game_id", nullable = false)
    var id: Int? = null

    @Column(name = "game_thread_id")
    var gameThreadId: String? = null

    @Column(name = "request_message_id")
    var requestMessageId: String? = null

    @Column(name = "subdivision")
    var subdivision: String? = null

    @Column(name = "season")
    var season: Int? = null

    @Column(name = "week")
    var week: Int? = null

    @Column(name = "series_game_number")
    var seriesGameNumber: Int = 1

    @Column(name = "game_type")
    var gameType: GameType = GameType.SCRIMMAGE

    @Column(name = "home_team")
    lateinit var homeTeam: String

    @Column(name = "away_team")
    lateinit var awayTeam: String

    @Column(name = "home_wins")
    var homeWins: Int = 0

    @Column(name = "home_losses")
    var homeLosses: Int = 0

    @Column(name = "away_wins")
    var awayWins: Int = 0

    @Column(name = "away_losses")
    var awayLosses: Int = 0

    @Column(name = "home_team_rank")
    var homeTeamRank: Int? = null

    @Column(name = "away_team_rank")
    var awayTeamRank: Int? = null

    @Column(name = "home_coach")
    lateinit var homeCoach: String

    @Column(name = "away_coach")
    lateinit var awayCoach: String

    @Column(name = "home_coach_discord_id")
    lateinit var homeCoachDiscordId: String

    @Column(name = "away_coach_discord_id")
    lateinit var awayCoachDiscordId: String

    @Column(name = "home_score")
    var homeScore: Int = 0

    @Column(name = "away_score")
    var awayScore: Int = 0

    @Column(name = "waiting_on")
    var waitingOn: TeamSide? = HOME

    @Column(name = "inning_half")
    var inningHalf: InningHalf = TOP

    @Column(name = "inning")
    var inning: Int = 1

    @Column(name = "outs")
    var outs: Int = 0

    @Column(name = "runner_on_first")
    var runnerOnFirst: Int? = null

    @Column(name = "runner_on_second")
    var runnerOnSecond: Int? = null

    @Column(name = "runner_on_third")
    var runnerOnThird: Int? = null

    @Column(name = "home_batter_lineup_spot")
    var homeBatterLineupSpot: Int? = null

    @Column(name = "away_batter_lineup_spot")
    var awayBatterLineupSpot: Int? = null

    @Column(name = "batter_name")
    var batterName: String? = null

    @Column(name = "batter_uniform_number")
    var batterUniformNumber: Int? = null

    @Column(name = "pitcher_name")
    var pitcherName: String? = null

    @Column(name = "pitcher_uniform_number")
    var pitcherUniformNumber: Int? = null

    @Column(name = "num_plate_appearance")
    var numPlateAppearance: Int? = 1

    @Column(name = "current_plate_appearance_id")
    var currentPlateAppearanceId: Int? = null

    @Column(name = "game_warned")
    var gameWarned: Boolean? = false

    @Column(name = "game_timer")
    var gameTimer: String? = null

    @Column(name = "timestamp", nullable = false)
    var timestamp: Instant? = null

    @Column(name = "last_message_timestamp")
    var lastMessageTimestamp: Instant? = null

    @Column(name = "close_game")
    var closeGame: Boolean? = false

    @Column(name = "close_game_pinged")
    var closeGamePinged: Boolean? = false

    @Column(name = "upset_alert")
    var upsetAlert: Boolean? = false

    @Column(name = "upset_alert_pinged")
    var upsetAlertPinged: Boolean? = false

    @Column(name = "game_status")
    var gameStatus: GameStatus = GameStatus.PREGAME

    // Default constructor
    constructor()

    // Constructor with parameters
    constructor(
        gameThreadId: String?,
        requestMessageId: String?,
        subdivision: String?,
        season: Int?,
        week: Int?,
        seriesGameNumber: Int,
        gameType: GameType,
        homeTeam: String,
        awayTeam: String,
        homeWins: Int,
        homeLosses: Int,
        awayWins: Int,
        awayLosses: Int,
        homeTeamRank: Int,
        awayTeamRank: Int,
        homeCoach: String,
        awayCoach: String,
        homeCoachDiscordId: String,
        awayCoachDiscordId: String,
        homeScore: Int,
        awayScore: Int,
        waitingOn: TeamSide,
        inningHalf: InningHalf,
        inning: Int,
        outs: Int,
        runnerOnFirst: Int?,
        runnerOnSecond: Int?,
        runnerOnThird: Int?,
        homeBatterLineupSpot: Int?,
        awayBatterLineupSpot: Int?,
        batterName: String?,
        batterUniformNumber: Int?,
        pitcherName: String?,
        pitcherUniformNumber: Int?,
        numPlateAppearance: Int? = 1,
        currentPlateAppearanceId: Int?,
        gameWarned: Boolean? = false,
        gameTimer: String?,
        timestamp: Instant? = Instant.now(),
        lastMessageTimestamp: Instant? = Instant.now(),
        closeGame: Boolean? = false,
        closeGamePinged: Boolean? = false,
        upsetAlert: Boolean? = false,
        upsetAlertPinged: Boolean? = false,
        gameStatus: GameStatus
    ) {
        this.gameThreadId = gameThreadId
        this.requestMessageId = requestMessageId
        this.subdivision = subdivision
        this.season = season
        this.week = week
        this.seriesGameNumber = seriesGameNumber
        this.gameType = gameType
        this.homeTeam = homeTeam
        this.awayTeam = awayTeam
        this.homeWins = homeWins
        this.homeLosses = homeLosses
        this.awayWins = awayWins
        this.awayLosses = awayLosses
        this.homeTeamRank = homeTeamRank
        this.awayTeamRank = awayTeamRank
        this.homeCoach = homeCoach
        this.awayCoach = awayCoach
        this.homeCoachDiscordId = homeCoachDiscordId
        this.awayCoachDiscordId = awayCoachDiscordId
        this.homeScore = homeScore
        this.awayScore = awayScore
        this.waitingOn = waitingOn
        this.inningHalf = inningHalf
        this.inning = inning
        this.outs = outs
        this.runnerOnFirst = runnerOnFirst
        this.runnerOnSecond = runnerOnSecond
        this.runnerOnThird = runnerOnThird
        this.homeBatterLineupSpot = homeBatterLineupSpot
        this.awayBatterLineupSpot = awayBatterLineupSpot
        this.batterName = batterName
        this.batterUniformNumber = batterUniformNumber
        this.pitcherName = pitcherName
        this.pitcherUniformNumber = pitcherUniformNumber
        this.numPlateAppearance = numPlateAppearance
        this.currentPlateAppearanceId = currentPlateAppearanceId
        this.gameWarned = gameWarned
        this.gameTimer = gameTimer
        this.timestamp = timestamp
        this.lastMessageTimestamp = lastMessageTimestamp
        this.closeGame = closeGame
        this.closeGamePinged = closeGamePinged
        this.upsetAlert = upsetAlert
        this.upsetAlertPinged = upsetAlertPinged
        this.gameStatus = gameStatus
    }

    enum class GameStatus(val description: String) {
        PREGAME("PREGAME"),
        IN_PROGRESS("IN PROGRESS"),
        FINAL("FINAL"),
        END_OF_REGULATION("END OF REGULATION"),
        EXTRA_INNINGS("EXTRA INNINGS"),
    }

    enum class Subdivision(val description: String) {
        FCB("FCB"),
        ;

        companion object {
            fun fromString(description: String): Subdivision? {
                return entries.find { it.description == description }
            }
        }
    }

    enum class Scenario(val description: String) {
        GAME_START("GAME START"),
        PLAY_RESULT("PLAY RESULT"),
        NORMAL_NUMBER_REQUEST("NORMAL NUMBER REQUEST"),
        DM_NUMBER_REQUEST("DM NUMBER REQUEST"),
        DELAY_OF_GAME_WARNING("DELAY OF GAME WARNING"),
        DELAY_OF_GAME_NOTIFICATION("DELAY OF GAME NOTIFICATION"),
        PREGAME_DELAY_OF_GAME_NOTIFICATION("PREGAME DELAY OF GAME NOTIFICATION"),
        DELAY_OF_GAME_HOME("DELAY OF GAME ON HOME TEAM"),
        DELAY_OF_GAME_AWAY("DELAY OF GAME ON AWAY TEAM"),
        GAME_OVER("GAME OVER"),
        PITCHER_CHANGE("PITCHER CHANGE"),
        BATTER_CHANGE("BATTER CHANGE"),
        STEAL_ATTEMPT("STEAL ATTEMPT"),
        STEAL_SUCCESS("STEAL SUCCESS"),
        STRIKEOUT("Strikeout"),
        WALK("Walk"),
        FLYOUT("Flyout"),
        GROUNDOUT("Groundout"),
        SINGLE("Single"),
        DOUBLE("Double"),
        TRIPLE("Triple"),
        HOME_RUN("Home Run"),
        ;

        companion object {
            fun fromString(description: String): Scenario? {
                return entries.find { it.description == description }
            }
        }
    }

    enum class ActualResult(val description: String) {
        STRIKEOUT("Strikeout"),
        WALK("Walk"),
        FLY_OUT("Fly out"),
        SACRIFICE_FLY("Sacrifice Fly"),
        GROUND_OUT("Ground out"),
        DOUBLE_PLAY("Double Play"),
        FIELDERS_CHOICE("Fielder's Choice"),
        SINGLE("Single"),
        DOUBLE("Double"),
        TRIPLE("Triple"),
        HOME_RUN("Home Run"),
        ;

        companion object {
            fun fromString(description: String): ActualResult? {
                return entries.find { it.description == description }
            }
        }
    }

    enum class InningHalf(val description: String) {
        TOP("TOP"),
        BOTTOM("BOTTOM"),
        ;

        companion object {
            fun fromString(description: String): InningHalf? {
                return entries.find { it.description == description }
            }
        }
    }

    enum class TeamSide(val description: String) {
        HOME("home"),
        AWAY("away"),
    }

    enum class GameType(val description: String) {
        OUT_OF_CONFERENCE("Out of Conference"),
        CONFERENCE_GAME("Conference Game"),
        CONFERENCE_TOURNAMENT("Conference Tournament"),
        CONFERENCE_CHAMPIONSHIP("Conference Championship"),
        REGIONAL("Regional"),
        REGIONAL_ELIMINATION("Regional Elimination"),
        SUPER_REGIONAL("Super Regional"),
        COLLEGE_WORLD_SERIES("College World Series"),
        COLLEGE_WORLD_SERIES_ELIMINATION("College World Series Elimination"),
        COLLEGE_WORLD_SERIES_CHAMPIONSHIP("College World Series Championship"),
        SCRIMMAGE("Scrimmage"),
        ;

        companion object {
            @JsonCreator
            fun fromDescription(description: String): GameType =
                entries.find { it.description.equals(description, ignoreCase = true) }
                    ?: throw IllegalArgumentException("Unknown game type: $description")
        }
    }
}