package com.fcb.porygon.domain

import com.fcb.porygon.domain.Game.ActualResult
import com.fcb.porygon.domain.Game.Scenario
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "plate_appearances", schema = "porygon")
open class PlateAppearance {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "plate_appearance_id", nullable = false)
    open var id: Int? = null

    @Column(name = "pitch_number")
    open var pitchNumber: Int? = null

    @Column(name = "home_team")
    open var homeTeam: String? = null

    @Column(name = "away_team")
    open var awayTeam: String? = null

    @Column(name = "home_score")
    open var homeScore: Int? = null

    @Column(name = "away_score")
    open var awayScore: Int? = null

    @Column(name = "inning_half")
    open var inningHalf: Game.InningHalf? = null

    @Column(name = "inning")
    open var inning: Int? = null

    @Column(name = "outs")
    open var outs: Int? = null

    @Column(name = "pitching_team")
    open var pitchingTeam: String? = null

    @Column(name = "batting_team")
    open var battingTeam: String? = null

    @Column(name = "pitcher_submitter")
    open var pitcherSubmitter: String? = null

    @Column(name = "batter_submitter")
    open var batterSubmitter: String? = null

    @Column(name = "batter_number_submission")
    open var batterNumberSubmission: String? = null

    @Column(name = "pitcher_number_submission")
    open var pitcherNumberSubmission: String? = null

    @Column(name = "difference")
    open var difference: Int? = null

    @Column(name = "submission_type")
    open var submission_type: SubmissionType? = null

    @Column(name = "result")
    open var result: Scenario? = null

    @Column(name = "actual_result")
    open var actualResult: ActualResult? = null

    @Column(name = "runner_on_first")
    open var runnerOnFirst: Int? = null

    @Column(name = "runner_on_second")
    open var runnerOnSecond: Int? = null

    @Column(name = "runner_on_third")
    open var runnerOnThird: Int? = null

    @Column(name = "runs_scored")
    open var runsScored: Int? = null

    @Column(name = "runner_on_first_after")
    open var runnerOnFirstAfter: Int? = null

    @Column(name = "runner_on_second_after")
    open var runnerOnSecondAfter: Int? = null

    @Column(name = "runner_on_third_after")
    open var runnerOnThirdAfter: Int? = null

    @Column(name = "lineup_spot")
    open var lineupSpot: Int? = null

    @Column(name = "batter_name")
    open var batterName: String? = null

    @Column(name = "batter_uniform_number")
    open var batterUniformNumber: Int? = null

    @Column(name = "pitcher_name")
    open var pitcherName: String? = null

    @Column(name = "pitcher_uniform_number")
    open var pitcherUniformNumber: Int? = null

    @Column(name = "win_probability")
    open var winProbability: Float? = null

    @Column(name = "win_probability_added")
    open var winProbabilityAdded: Float? = null

    @Column(name = "batter_response_speed")
    open var batterResponseSpeed: Long? = null

    @Column(name = "pitcher_response_speed")
    open var pitcherResponseSpeed: Long? = null

    @Column(name = "plate_appearance_finished")
    open var plateAppearanceFinished: Boolean? = false

    enum class SubmissionType(val description: String) {
        SWING("Swing"),
        BUNT("Bunt"),
        STEAL("Steal"),
        INTENTIONAL_WALK("Intentional Walk"),
        PINCH_HIT("Pinch Hit"),
        PINCH_RUN("Pinch Run"),
        PITCHER_CHANGE("Pitcher Change"),
    }
}