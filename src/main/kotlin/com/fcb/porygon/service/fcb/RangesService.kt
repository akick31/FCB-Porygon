package com.fcb.porygon.service.fcfb

import com.fcb.porygon.domain.PlateAppearance.SubmissionType
import com.fcb.porygon.domain.Player.Archetype
import com.fcb.porygon.repositories.RangesRepository
import com.fcb.porygon.utils.ResultNotFoundException
import org.springframework.stereotype.Service

@Service
class RangesService(
    private val rangesRepository: RangesRepository,
) {
    /**
     * Get the result of a normal plate appearance
     * @param submissionType
     * @param batterArchetype
     * @param pitcherArchetype
     * @param difference
     */
    fun getResult(
        submissionType: SubmissionType,
        batterArchetype: Archetype,
        pitcherArchetype: Archetype,
        difference: Int,
    ) = rangesRepository.getNormalResult(
        submissionType,
        batterArchetype,
        pitcherArchetype,
        difference.toString(),
    ) ?: throw ResultNotFoundException()
}
