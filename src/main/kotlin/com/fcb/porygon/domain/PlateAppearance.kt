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
class PlateAppearance {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "plate_appearance_id", nullable = false)
    var id: Int? = null

    @Column(name = "pitch_number")
    var pitchNumber: Int? = null

    @Column(name = "home_team")
    var homeTeam: String? = null

    @Column(name = "away_team")
    var awayTeam: String? = null

    @Column(name = "home_score")
    var homeScore: Int? = null

    @Column(name = "away_score")
    var awayScore: Int? = null

    @Column(name = "inning_half")
    var inningHalf: Game.InningHalf? = null

    @Column(name = "inning")
    var inning: Int? = null

    @Column(name = "outs")
    var outs: Int? = null

    @Column(name = "pitching_team")
    var pitchingTeam: String? = null

    @Column(name = "batting_team")
    var battingTeam: String? = null

    @Column(name = "pitcher_submitter")
    var pitcherSubmitter: String? = null

    @Column(name = "batter_submitter")
    var batterSubmitter: String? = null

    @Column(name = "batter_number_submission")
    var batterNumberSubmission: String? = null

    @Column(name = "pitcher_number_submission")
    var pitcherNumberSubmission: String? = null

    @Column(name = "difference")
    var difference: Int? = null

    @Column(name = "submission_type")
    var submissionType: SubmissionType? = null

    @Column(name = "result")
    var result: Scenario? = null

    @Column(name = "actual_result")
    var actualResult: ActualResult? = null

    @Column(name = "runner_on_first")
    var runnerOnFirst: Int? = null

    @Column(name = "runner_on_second")
    var runnerOnSecond: Int? = null

    @Column(name = "runner_on_third")
    var runnerOnThird: Int? = null

    @Column(name = "runs_scored")
    var runsScored: Int = 0

    @Column(name = "runner_on_first_after")
    var runnerOnFirstAfter: Int? = null

    @Column(name = "runner_on_second_after")
    var runnerOnSecondAfter: Int? = null

    @Column(name = "runner_on_third_after")
    var runnerOnThirdAfter: Int? = null

    @Column(name = "lineup_spot")
    var lineupSpot: Int? = null

    @Column(name = "batter_name")
    var batterName: String? = null

    @Column(name = "batter_uniform_number")
    var batterUniformNumber: Int? = null

    @Column(name = "pitcher_name")
    var pitcherName: String? = null

    @Column(name = "pitcher_uniform_number")
    var pitcherUniformNumber: Int? = null

    @Column(name = "win_probability")
    var winProbability: Float? = null

    @Column(name = "win_probability_added")
    var winProbabilityAdded: Float? = null

    @Column(name = "batter_response_speed")
    var batterResponseSpeed: Long? = null

    @Column(name = "pitcher_response_speed")
    var pitcherResponseSpeed: Long? = null

    @Column(name = "plate_appearance_finished")
    var plateAppearanceFinished: Boolean? = false

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
