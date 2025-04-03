package com.fcb.porygon.service.fcb

import com.fcb.porygon.domain.Game
import com.fcb.porygon.domain.Game.ActualResult
import com.fcb.porygon.domain.Game.BaseCondition
import com.fcb.porygon.domain.Game.InningHalf
import com.fcb.porygon.domain.Game.InningHalf.TOP
import com.fcb.porygon.domain.Game.Scenario
import com.fcb.porygon.domain.Game.Scenario.DOUBLE
import com.fcb.porygon.domain.Game.Scenario.FLYOUT
import com.fcb.porygon.domain.Game.Scenario.HOME_RUN
import com.fcb.porygon.domain.Game.Scenario.LEFT_GROUNDOUT
import com.fcb.porygon.domain.Game.Scenario.RIGHT_GROUNDOUT
import com.fcb.porygon.domain.Game.Scenario.SINGLE
import com.fcb.porygon.domain.Game.Scenario.STRIKEOUT
import com.fcb.porygon.domain.Game.Scenario.TRIPLE
import com.fcb.porygon.domain.PlateAppearance
import com.fcb.porygon.domain.PlateAppearance.SubmissionType
import com.fcb.porygon.domain.Player
import com.fcb.porygon.domain.Player.Archetype
import com.fcb.porygon.models.game.PlateAppearanceOutcome
import com.fcb.porygon.repositories.PlateAppearanceRepository
import com.fcb.porygon.service.fcfb.RangesService
import com.fcb.porygon.service.fcfb.ScorebugService
import com.fcb.porygon.utils.EncryptionUtils
import com.fcb.porygon.utils.InvalidScenarioException
import com.fcb.porygon.utils.Logger
import com.fcb.porygon.utils.PitcherNumberSubmissionNotFound
import com.fcb.porygon.utils.PlateAppearanceNotFoundException
import com.fcb.porygon.utils.ResultNotFoundException
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class PlateAppearanceService(
    private val plateAppearanceRepository: PlateAppearanceRepository,
    private val encryptionUtils: EncryptionUtils,
    private val gameService: GameService,
    private val gameStatsService: GameStatsService,
    private val rangesService: RangesService,
    private val scorebugService: ScorebugService,
) {
    /**
     * Start a new plate appearance, the pitching number was submitted. The pitching number is encrypted
     * @param gameId
     * @param pitcherSubmitter
     * @param pitcherNumberSubmission
     * @return
     */
    fun pitchingNumberSubmitted(
        gameId: Int,
        pitcherSubmitter: String,
        pitcherNumberSubmission: Int,
        submissionType: SubmissionType?
    ): PlateAppearance {
        try {
            val game = gameService.getGameById(gameId)
            val responseSpeed =
                if (game.gameStatus != Game.GameStatus.PREGAME) {
                    getResponseSpeed(game)
                } else {
                    null
                }

            val encryptedPitchNumber = encryptionUtils.encrypt(pitcherNumberSubmission.toString())

            val plateAppearance =
                savePlateAppearance(
                    PlateAppearance(
                        gameId = game.id,
                        pitchNumber = game.numPlateAppearance.plus(1),
                        homeTeam = game.homeTeam,
                        awayTeam = game.awayTeam,
                        homeScore = game.homeScore,
                        awayScore = game.awayScore,
                        inningHalf = game.inningHalf,
                        inning = game.inning,
                        outs = game.outs,
                        pitchingTeam = if (game.inningHalf == TOP) game.homeTeam else game.awayTeam,
                        battingTeam = if (game.inningHalf == TOP) game.awayTeam else game.homeTeam,
                        pitcherSubmitter = pitcherSubmitter,
                        batterSubmitter = null,
                        batterNumberSubmission = null,
                        pitcherNumberSubmission = encryptedPitchNumber,
                        difference = null,
                        submissionType = submissionType,
                        result = null,
                        actualResult = null,
                        runnerOnFirst = game.runnerOnFirst,
                        runnerOnSecond = game.runnerOnSecond,
                        runnerOnThird = game.runnerOnThird,
                        runsScored = 0,
                        runnerOnFirstAfter = null,
                        runnerOnSecondAfter = null,
                        runnerOnThirdAfter = null,
                        lineupSpot = if (game.inningHalf == TOP) game.awayBatterLineupSpot else game.homeBatterLineupSpot,
                        batterName = game.batterName,
                        batterUniformNumber = game.batterUniformNumber,
                        pitcherName = game.pitcherName,
                        pitcherUniformNumber = game.pitcherUniformNumber,
                        winProbability = 0.0F,
                        winProbabilityAdded = 0.0F,
                        batterResponseSpeed = null,
                        pitcherResponseSpeed = responseSpeed,
                        plateAppearanceFinished = false
                    ),
                )

            gameService.updateWithPitcherNumberSubmission(game, plateAppearance)
            return plateAppearance
        } catch (e: Exception) {
            Logger.error("There was an error submitting the pitching number for game $gameId: " + e.message)
            throw e
        }
    }

    /**
     * The batting number was submitted, run the plate appearance
     * @param gameId
     * @param batterSubmitter
     * @param batterNumberSubmission
     * @param submissionType
     * @return
     */
    fun batterNumberSubmitted(
        gameId: Int,
        batterSubmitter: String,
        batterNumberSubmission: Int,
        submissionType: SubmissionType,
    ): PlateAppearance {
        try {
            val game = gameService.getGameById(gameId)
            var plateAppearance = getPlateAppearanceById(game.currentPlateAppearanceId!!)
            val responseSpeed = getResponseSpeed(game)
            val pitcherSubmissionType = plateAppearance.submissionType

            plateAppearance.batterResponseSpeed = responseSpeed
            plateAppearance.batterSubmitter = batterSubmitter

            val descryptedPitcherNumber = encryptionUtils.decrypt(plateAppearance.pitcherNumberSubmission
                ?: throw PitcherNumberSubmissionNotFound()
            )

            if (pitcherSubmissionType != null) {
                when (pitcherSubmissionType) {
                    SubmissionType.INTENTIONAL_WALK -> plateAppearance =
                        runIntentionalWalk(
                            plateAppearance,
                            game,
                            submissionType,
                            batterNumberSubmission,
                            descryptedPitcherNumber
                        )
                    else -> {}
                }
            } else {
                when (submissionType) {
                    SubmissionType.SWING -> plateAppearance =
                        swing(
                            plateAppearance,
                            game,
                            submissionType,
                            batterNumberSubmission,
                            descryptedPitcherNumber
                        )
                    SubmissionType.BUNT -> plateAppearance =
                        bunt(
                            plateAppearance,
                            game,
                            submissionType,
                            batterNumberSubmission,
                            descryptedPitcherNumber
                        )
                    SubmissionType.STEAL -> plateAppearance =
                        steal(
                            plateAppearance,
                            game,
                            submissionType,
                            batterNumberSubmission,
                            descryptedPitcherNumber
                        )
                    else -> {}
                }
            }

            return plateAppearance
        } catch (e: Exception) {
            Logger.error("There was an error submitting the batting number for game $gameId: " + e.message)
            throw e
        }
    }

    /**
     * Rollback the plate appearance to the previous plate appearance
     * @param gameId
     */
    fun rollbackPlateAppearance(gameId: Int): PlateAppearance {
        try {
            val game = gameService.getGameById(gameId)
            val previousPlateAppearance = getPreviousPlateAppearance(gameId)
            val plateAppearance = getPlateAppearanceById(game.currentPlateAppearanceId!!)
            gameService.rollbackPlateAppearance(game, previousPlateAppearance, plateAppearance)
            plateAppearanceRepository.deleteById(plateAppearance.id)
            return previousPlateAppearance
        } catch (e: Exception) {
            Logger.error("There was an error rolling back the play for game $gameId: " + e.message)
            throw e
        }
    }

    /**
     * Get the response speed
     * @param game
     */
    private fun getResponseSpeed(game: Game): Long {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val parsedDateTime = game.lastMessageTimestamp?.let { LocalDateTime.parse(it, formatter) }
        val currentDateTime = LocalDateTime.now()
        return Duration.between(parsedDateTime, currentDateTime).seconds
    }

    /**
     * Get a plate appearances by its id
     * @param playId
     * @return
     */
    private fun getPlateAppearanceById(playId: Int) =
        plateAppearanceRepository.getPlateAppearanceById(playId)
            ?: throw PlateAppearanceNotFoundException("PlateAppearanceRepository with id $playId not found")

    /**
     * Get the previous plate appearances of a game
     * @param gameId
     * @return
     */
    fun getPreviousPlateAppearance(gameId: Int) =
        plateAppearanceRepository.getPreviousPlateAppearance(gameId)
            ?: throw PlateAppearanceNotFoundException("No previous play found for game $gameId")

    /**
     * Get the current plate appearances of a game
     * @param gameId
     */
    fun getCurrentPlateAppearance(gameId: Int) =
        plateAppearanceRepository.getCurrentPlateAppearance(gameId)
            ?: throw PlateAppearanceNotFoundException("No current play found for game $gameId")

    /**
     * Get the current plate appearances of a game or null
     * @param gameId
     */
    fun getCurrentPlateAppearanceOrNull(gameId: Int) = plateAppearanceRepository.getCurrentPlateAppearance(gameId)

    /**
     * Get all plate appearances for a game
     * @param gameId
     */
    fun getAllPlateAppearancesByGameId(gameId: Int) =
        plateAppearanceRepository.getAllPlateAppearancesByGameId(gameId).ifEmpty {
            throw PlateAppearanceNotFoundException("No plate appearances found for game $gameId")
        }

    /**
     * Get all plate appearances with a user
     * @param discordTag
     */
    fun getAllPlateAppearancesByDiscordTag(discordTag: String) =
        plateAppearanceRepository.getAllPlateAppearancesByDiscordTag(discordTag).ifEmpty {
            throw PlateAppearanceNotFoundException("No plate appearances found for user $discordTag")
        }

    /**
     * Get the number of delay of game instances for a home team
     * @param gameId
     */
    fun getHomeDelayOfGameInstances(gameId: Int) =
        plateAppearanceRepository.getHomeDelayOfGameInstances(gameId)
            ?: throw PlateAppearanceNotFoundException("No delay of game instances found for game $gameId")

    /**
     * Get the number of delay of game instances for an away team
     * @param gameId
     */
    fun getAwayDelayOfGameInstances(gameId: Int) =
        plateAppearanceRepository.getAwayDelayOfGameInstances(gameId)
            ?: throw PlateAppearanceNotFoundException("No delay of game instances found for game $gameId")

    /**
     * Get the average response time for a user
     * @param discordTag
     * @param season
     */
    fun getUserAverageResponseTime(
        discordTag: String,
        season: Int,
    ) = plateAppearanceRepository.getUserAverageResponseTime(discordTag, season)
        ?: throw Exception("Could not get average response time for user $discordTag")

    /**
     * Runs the play, returns the updated gamePlay
     * @param plateAppearance
     * @param game
     * @param submissionType
     * @param batterNumberSubmission
     * @param decryptedPitcherNumber
     * @return
     */
    private fun swing(
        plateAppearance: PlateAppearance,
        game: Game,
        submissionType: SubmissionType,
        batterNumberSubmission: Int,
        decryptedPitcherNumber: String,
    ): PlateAppearance {
        val difference = gameService.getDifference(batterNumberSubmission, decryptedPitcherNumber.toInt())
        val batter = playerService.getPlayerByNumberAndTeam(
            plateAppearance.battingTeam,
            plateAppearance.batterUniformNumber,
        )
        val pitcher = playerService.getPlayerByNumberAndTeam(
            plateAppearance.pitchingTeam,
            decryptedPitcherNumber,
        )
        val resultInformation = rangesService.getResult(
            submissionType,
            batter.archetype,
            pitcher.archetype,
            difference
        )
        var result = resultInformation.result ?: throw ResultNotFoundException()
        var homeScore = game.homeScore
        var awayScore = game.awayScore
        var inning = game.inning
        var inningHalf = game.inningHalf
        var outs = game.outs
        var runsScored = 0
        var runnerOnFirst = playerService.getPlayerByNumberAndTeam(
            plateAppearance.battingTeam,
            plateAppearance.runnerOnFirst,
        )
        var runnerOnSecond = playerService.getPlayerByNumberAndTeam(
            plateAppearance.battingTeam,
            plateAppearance.runnerOnSecond,
        )
        var runnerOnThird = playerService.getPlayerByNumberAndTeam(
            plateAppearance.battingTeam,
            plateAppearance.runnerOnThird,
        )
        var baseConditionBefore = gameService.getBaseCondition(
            runnerOnFirst,
            runnerOnSecond,
            runnerOnThird,
        )
        var runnerOnFirstAfter = runnerOnFirst
        var runnerOnSecondAfter = runnerOnSecond
        var runnerOnThirdAfter = runnerOnThird
        var baseConditionAfter = baseConditionBefore
        var actualResult: ActualResult
        val outcome = when (result) {
            STRIKEOUT -> {
                handleStrikeout(
                    outs,
                    baseConditionBefore,
                    runnerOnFirst,
                    runnerOnSecond,
                    runnerOnThird,
                    homeScore,
                    awayScore,
                )
            }
            Scenario.WALK -> {
                handleWalk(
                    outs,
                    inningHalf,
                    baseConditionBefore,
                    runnerOnFirst,
                    runnerOnSecond,
                    runnerOnThird,
                    homeScore,
                    awayScore,
                )
            }
            FLYOUT -> {
                handleFlyout(
                    outs,
                    inningHalf,
                    baseConditionBefore,
                    runnerOnFirst,
                    runnerOnSecond,
                    runnerOnThird,
                    homeScore,
                    awayScore,
                )
            }
            LEFT_GROUNDOUT -> {
                handleLeftGroundout(
                    outs,
                    inningHalf,
                    baseConditionBefore,
                    runnerOnFirst,
                    runnerOnSecond,
                    runnerOnThird,
                    homeScore,
                    awayScore,
                )
            }
            RIGHT_GROUNDOUT -> {
                handleRightGroundout(
                    outs,
                    inningHalf,
                    baseConditionBefore,
                    runnerOnFirst,
                    runnerOnSecond,
                    runnerOnThird,
                    homeScore,
                    awayScore,
                )
            }
            SINGLE -> {
                handleSingle(
                    outs,
                    inningHalf,
                    baseConditionBefore,
                    runnerOnFirst,
                    runnerOnSecond,
                    runnerOnThird,
                    homeScore,
                    awayScore,
                )
            }
            DOUBLE -> {
                handleDouble(
                    outs,
                    inningHalf,
                    baseConditionBefore,
                    runnerOnFirst,
                    runnerOnSecond,
                    runnerOnThird,
                    homeScore,
                    awayScore,
                )
            }
            TRIPLE -> {
                handleTriple(
                    outs,
                    inningHalf,
                    baseConditionBefore,
                    runnerOnFirst,
                    runnerOnSecond,
                    runnerOnThird,
                    homeScore,
                    awayScore,
                )
            }
            HOME_RUN -> {
                handleHomeRun(
                    outs,
                    inningHalf,
                    baseConditionBefore,
                    runnerOnFirst,
                    runnerOnSecond,
                    runnerOnThird,
                    homeScore,
                    awayScore,
                )
            }
            else -> throw InvalidScenarioException()
        }

        gameService.updateGameValues(
            game,
            outcome
        )

        scorebugService.generateScorebug(game)

        val allPlateAppearances = plateAppearanceRepository.getAllPlateAppearancesByGameId(game.id)
        gameStatsService.updateGameStats(
            game,
            allPlateAppearances,
            plateAppearance,
        )

        return updatePlateAppearanceValues(
            plateAppearance,
            submissionType,
            result,
            outcome,
            decryptedPitcherNumber,
            batterNumberSubmission,
            difference
        )
    }

    /**
     * Handle the strikeout scenario
     * @param outs
     * @param baseConditionBefore
     * @param runnerOnFirst
     * @param runnerOnSecond
     * @param runnerOnThird
     * @param homeScore
     * @param awayScore
     */
    private fun handleStrikeout(
        outs: Int,
        baseConditionBefore: BaseCondition,
        runnerOnFirst: Player?,
        runnerOnSecond: Player?,
        runnerOnThird: Player?,
        homeScore: Int,
        awayScore: Int,
    ): PlateAppearanceOutcome {
        val actualResult = ActualResult.STRIKEOUT
        val updatedOuts = outs + 1
        val runsScored = 0
        val runnerOnFirstAfter: Player?
        val runnerOnSecondAfter: Player?
        val runnerOnThirdAfter: Player?
        var baseConditionAfter = baseConditionBefore
        if (updatedOuts < 3) {
            runnerOnFirstAfter = runnerOnFirst
            runnerOnSecondAfter = runnerOnSecond
            runnerOnThirdAfter = runnerOnThird
        } else {
            runnerOnFirstAfter = null
            runnerOnSecondAfter = null
            runnerOnThirdAfter = null
            baseConditionAfter = BaseCondition.EMPTY
        }
        return PlateAppearanceOutcome(
            actualResult = actualResult,
            outs = updatedOuts,
            runsScored = runsScored,
            homeScore = homeScore,
            awayScore = awayScore,
            runnerOnFirstAfter = runnerOnFirstAfter,
            runnerOnSecondAfter = runnerOnSecondAfter,
            runnerOnThirdAfter = runnerOnThirdAfter,
            baseConditionAfter = baseConditionAfter,
        )
    }

    /**
     * Handle the flyout scenario
     * @param outs
     * @param inningHalf
     * @param baseConditionBefore
     * @param runnerOnFirst
     * @param runnerOnSecond
     * @param runnerOnThird
     * @param homeScore
     * @param awayScore
     */
    private fun handleFlyout(
        outs: Int,
        inningHalf: InningHalf,
        baseConditionBefore: BaseCondition,
        runnerOnFirst: Player?,
        runnerOnSecond: Player?,
        runnerOnThird: Player?,
        homeScore: Int,
        awayScore: Int,
    ): PlateAppearanceOutcome {
        var actualResult = ActualResult.FLYOUT
        val updatedOuts = outs + 1
        var runsScored = 0
        var updatedHomeScore = homeScore
        var updatedAwayScore = awayScore
        var runnerOnFirstAfter = runnerOnFirst
        var runnerOnSecondAfter = runnerOnSecond
        var runnerOnThirdAfter = runnerOnThird
        var baseConditionAfter = baseConditionBefore
        if (updatedOuts < 3) {
            when (baseConditionBefore) {
                BaseCondition.FIRST -> {
                    runnerOnFirstAfter = runnerOnFirst
                }
                BaseCondition.SECOND -> {
                    if (runnerOnSecond?.archetype == Archetype.SPEEDY) {
                        runnerOnSecondAfter = null
                        runnerOnThirdAfter = runnerOnSecond
                        baseConditionAfter = BaseCondition.THIRD

                    } else {
                        runnerOnSecondAfter = runnerOnSecond
                    }
                }
                BaseCondition.THIRD -> {
                    actualResult = ActualResult.SACRIFICE_FLY
                    runnerOnThirdAfter = null
                    baseConditionAfter = BaseCondition.EMPTY
                    runsScored = 1
                    if (inningHalf == TOP) {
                        updatedAwayScore += 1
                    } else {
                        updatedHomeScore += 1
                    }
                }
                BaseCondition.FIRST_SECOND -> {
                    if (runnerOnSecond?.archetype == Archetype.SPEEDY) {
                        runnerOnFirstAfter = runnerOnFirst
                        runnerOnSecondAfter = null
                        runnerOnThirdAfter = runnerOnSecond
                        baseConditionAfter = BaseCondition.FIRST_THIRD
                    } else {
                        runnerOnFirstAfter = runnerOnFirst
                        runnerOnSecondAfter = runnerOnSecond
                        baseConditionAfter = BaseCondition.FIRST_SECOND
                    }
                }
                BaseCondition.FIRST_THIRD -> {
                    actualResult = ActualResult.SACRIFICE_FLY
                    runnerOnFirstAfter = runnerOnFirst
                    runnerOnThirdAfter = null
                    baseConditionAfter = BaseCondition.FIRST
                    runsScored = 1
                    if (inningHalf == TOP) {
                        updatedAwayScore += 1
                    } else {
                        updatedHomeScore += 1
                    }
                }
                BaseCondition.SECOND_THIRD -> {
                    actualResult = ActualResult.SACRIFICE_FLY
                    if (runnerOnSecond?.archetype == Archetype.SPEEDY) {
                        runnerOnSecondAfter = null
                        runnerOnThirdAfter = runnerOnSecond
                        baseConditionAfter = BaseCondition.THIRD
                    } else {
                        runnerOnSecondAfter = runnerOnSecond
                        runnerOnThirdAfter = null
                        baseConditionAfter = BaseCondition.SECOND
                    }
                    runsScored = 1
                    if (inningHalf == TOP) {
                        updatedAwayScore += 1
                    } else {
                        updatedHomeScore += 1
                    }
                }
                BaseCondition.BASED_LOADED -> {
                    actualResult = ActualResult.SACRIFICE_FLY
                    if (runnerOnSecond?.archetype == Archetype.SPEEDY) {
                        runnerOnFirstAfter = runnerOnFirst
                        runnerOnSecondAfter = null
                        runnerOnThirdAfter = runnerOnSecond
                        baseConditionAfter = BaseCondition.FIRST_THIRD
                    } else {
                        runnerOnFirstAfter = runnerOnFirst
                        runnerOnSecondAfter = runnerOnSecond
                        runnerOnThirdAfter = null
                        baseConditionAfter = BaseCondition.FIRST_SECOND
                    }
                    runsScored = 1
                    if (inningHalf == TOP) {
                        updatedAwayScore += 1
                    } else {
                        updatedHomeScore += 1
                    }
                }
                else -> {}
            }
        } else {
            runnerOnFirstAfter = null
            runnerOnSecondAfter = null
            runnerOnThirdAfter = null
            baseConditionAfter = BaseCondition.EMPTY
        }
        return PlateAppearanceOutcome(
            actualResult = actualResult,
            outs = updatedOuts,
            runsScored = runsScored,
            homeScore = updatedHomeScore,
            awayScore = updatedAwayScore,
            runnerOnFirstAfter = runnerOnFirstAfter,
            runnerOnSecondAfter = runnerOnSecondAfter,
            runnerOnThirdAfter = runnerOnThirdAfter,
            baseConditionAfter = baseConditionAfter,
        )
    }

    /**
     * Update the plate appearance values
     * @param plateAppearance
     * @param submissionType
     * @param result
     * @param outcome
     * @param decryptedPitcherNumber
     * @param batterNumberSubmission
     * @param difference
     * @return
     */
    private fun updatePlateAppearanceValues(
        plateAppearance: PlateAppearance,
        submissionType: SubmissionType,
        result: Scenario,
        outcome: PlateAppearanceOutcome,
        decryptedPitcherNumber: Int,
        batterNumberSubmission: Int,
        difference: Int
    ): PlateAppearance {
        plateAppearance.homeScore = outcome.homeScore
        plateAppearance.awayScore = outcome.awayScore
        plateAppearance.batterNumberSubmission = batterNumberSubmission.toString()
        plateAppearance.pitcherNumberSubmission = decryptedPitcherNumber.toString()
        plateAppearance.difference = difference
        plateAppearance.submissionType = submissionType
        plateAppearance.result = result
        plateAppearance.actualResult = outcome.actualResult
        plateAppearance.runsScored = outcome.runsScored
        plateAppearance.runnerOnFirstAfter = outcome.runnerOnFirstAfter?.uniformNumber
        plateAppearance.runnerOnSecondAfter = outcome.runnerOnSecondAfter?.uniformNumber
        plateAppearance.runnerOnThirdAfter = outcome.runnerOnThirdAfter?.uniformNumber
        plateAppearance.plateAppearanceFinished = true

        return savePlateAppearance(plateAppearance)
    }

    /**
     * Update the batter and pitcher information for the game
     * @param plateAppearance
     */
    private fun updateBatterAndPitcher(
        plateAppearance: PlateAppearance,
    ) {
        val batter = lineupService.getBatterByLineupSpotAndTeam(
            plateAppearance.battingTeam,
            plateAppearance.lineupSpot,
        )
        val pitcher = lineupService.getPitcherByTeam(
            plateAppearance.pitchingTeam,
        )
        plateAppearance.batterName = batter.name
        plateAppearance.batterUniformNumber = batter.uniformNumber
        plateAppearance.pitcherName = pitcher.name
        plateAppearance.pitcherUniformNumber = pitcher.uniformNumber
    }

    /**
     * Handle the normal end of quarter scenarios
     * @param game
     * @param gamePlay
     * @param actualResult
     * @param initialPossession
     * @param initialClock
     * @param initialQuarter
     * @param homeScore
     * @param awayScore
     * @param playTime
     */
    private fun handleEndOfQuarterNormalScenarios(
        game: Game,
        gamePlay: Play,
        actualResult: ActualResult,
        initialPossession: TeamSide,
        initialClock: Int,
        initialQuarter: Int,
        homeScore: Int,
        awayScore: Int,
        playTime: Int,
    ): Triple<TeamSide, Int, Int> {
        var possession = initialPossession
        var clock = initialClock
        var quarter = initialQuarter

        // If quarter is over but game is not over
        if (clock <= 0 && !gameService.isTouchdownPlay(actualResult) && quarter < 4) {
            quarter += 1
            clock = 420 - playTime
            if (quarter == 3) {
                possession = gameService.handleHalfTimePossessionChange(game)
                clock = 420
            }
        } else if (clock <= 0 && !gameService.isTouchdownPlay(actualResult) && gamePlay.quarter == 4) {
            // Check if game is over or needs to go to OT
            quarter =
                if (homeScore > awayScore || awayScore > homeScore) {
                    0
                } else {
                    5
                }
            clock = 0
        } else if ((clock - playTime) <= 0 && gameService.isTouchdownPlay(actualResult)) {
            clock = 0
            if (quarter == 4 &&
                ((homeScore - awayScore) >= 2 || (awayScore - homeScore) >= 2)
            ) {
                quarter = 0
            }
        } else if (clock > 0) {
            clock = initialClock - playTime
            if (clock <= 0 && !gameService.isTouchdownPlay(actualResult) && quarter < 4) {
                quarter += 1
                clock = 420
                if (quarter == 3) {
                    possession = gameService.handleHalfTimePossessionChange(game)
                }
            } else if (clock <= 0 && !gameService.isTouchdownPlay(actualResult) && gamePlay.quarter == 4) {
                // Check if game is over or needs to go to OT
                quarter =
                    if (homeScore > awayScore || awayScore > homeScore) {
                        0
                    } else {
                        5
                    }
                clock = 0
            } else if (clock <= 0 && gameService.isTouchdownPlay(actualResult)) {
                clock = 0
                if (quarter == 4 &&
                    ((homeScore - awayScore) >= 2 || (awayScore - homeScore) >= 2)
                ) {
                    quarter = 0
                }
            }
        }
        return Triple(possession, clock, quarter)
    }

    /**
     * Save the plate appearance
     */
    private fun savePlateAppearance(plateAppearance: PlateAppearance) =
        plateAppearanceRepository.save(plateAppearance)
}
