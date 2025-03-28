package com.fcb.porygon.domain

import com.fcb.porygon.domain.PlateAppearance.SubmissionType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "ranges", schema = "porygon")
open class Ranges {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Int? = null

    @Column(name = "batter_archetype")
    open var batterArchetype: String? = null

    @Column(name = "pitcher_archetype")
    open var pitcherArchetype: String? = null

    @Column(name = "submission_type")
    open var submissionType: SubmissionType? = null

    @Column(name = "result")
    open var result: String? = null

    @Column(name = "low_range")
    open var lowRange: Int? = null

    @Column(name = "high_range")
    open var highRange: Int? = null
}
