package com.fcb.porygon.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "schedule", schema = "porygon")
open class Schedule {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Int? = null

    @Column(name = "season", nullable = false)
    open var season: Int? = null

    @Column(name = "week", nullable = false)
    open var week: Int? = null

    @Column(name = "game_number", nullable = false)
    open var gameNumber: Int? = null

    @Column(name = "subdivision", nullable = false)
    open var subdivision: String? = null

    @Column(name = "home_team", nullable = false)
    open var homeTeam: String? = null

    @Column(name = "away_team", nullable = false)
    open var awayTeam: String? = null

    @Column(name = "game_type", nullable = false)
    open var gameType: String? = null

    @Column(name = "started", nullable = false)
    open var started: Boolean? = false

    @Column(name = "finished")
    open var finished: Boolean? = false
}