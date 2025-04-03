package com.fcb.porygon.service.fcb

import com.fcb.porygon.domain.Game
import com.fcb.porygon.repositories.GameRepository
import com.fcb.porygon.service.fcb.GameSpecificationService.GameCategory.ONGOING
import com.fcb.porygon.service.fcb.GameSpecificationService.GameCategory.PAST
import com.fcb.porygon.service.fcb.GameSpecificationService.GameCategory.PAST_SCRIMMAGE
import com.fcb.porygon.service.fcb.GameSpecificationService.GameCategory.SCRIMMAGE
import com.fcb.porygon.service.fcb.GameSpecificationService.GameFilter.COLLEGE_WORLD_SERIES
import com.fcb.porygon.service.fcb.GameSpecificationService.GameFilter.COLLEGE_WORLD_SERIES_CHAMPIONSHIP
import com.fcb.porygon.service.fcb.GameSpecificationService.GameFilter.COLLEGE_WORLD_SERIES_ELIMINATION
import com.fcb.porygon.service.fcb.GameSpecificationService.GameFilter.CONFERENCE_CHAMPIONSHIP
import com.fcb.porygon.service.fcb.GameSpecificationService.GameFilter.CONFERENCE_GAME
import com.fcb.porygon.service.fcb.GameSpecificationService.GameFilter.CONFERENCE_TOURNAMENT
import com.fcb.porygon.service.fcb.GameSpecificationService.GameFilter.EXTRA_INNINGS
import com.fcb.porygon.service.fcb.GameSpecificationService.GameFilter.IN_PROGRESS
import com.fcb.porygon.service.fcb.GameSpecificationService.GameFilter.OUT_OF_CONFERENCE
import com.fcb.porygon.service.fcb.GameSpecificationService.GameFilter.PREGAME
import com.fcb.porygon.service.fcb.GameSpecificationService.GameFilter.RANKED_GAME
import com.fcb.porygon.service.fcb.GameSpecificationService.GameFilter.REGIONAL
import com.fcb.porygon.service.fcb.GameSpecificationService.GameFilter.REGIONAL_ELIMINATION
import com.fcb.porygon.service.fcb.GameSpecificationService.GameFilter.SUPER_REGIONAL
import com.fcb.porygon.service.fcb.GameSpecificationService.GameSort.CLOSEST_TO_END
import com.fcb.porygon.service.fcb.GameSpecificationService.GameSort.MOST_TIME_REMAINING
import com.fcb.porygon.service.fcfb.TeamService
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

@Service
class GameSpecificationService(
    private val teamService: TeamService,
    private val gameRepository: GameRepository,
) {
    enum class GameFilter {
        RANKED_GAME,
        CONFERENCE_GAME,
        OUT_OF_CONFERENCE,
        CONFERENCE_TOURNAMENT,
        CONFERENCE_CHAMPIONSHIP,
        REGIONAL,
        REGIONAL_ELIMINATION,
        SUPER_REGIONAL,
        COLLEGE_WORLD_SERIES,
        COLLEGE_WORLD_SERIES_ELIMINATION,
        COLLEGE_WORLD_SERIES_CHAMPIONSHIP,
        PREGAME,
        IN_PROGRESS,
        EXTRA_INNINGS,
    }

    enum class GameCategory {
        ONGOING,
        PAST,
        SCRIMMAGE,
        PAST_SCRIMMAGE,
    }

    enum class GameSort {
        CLOSEST_TO_END,
        MOST_TIME_REMAINING,
    }

    /**
     * Create the spec for a game
     * @param filters
     * @param conference
     * @param season
     * @param week
     */
    fun createSpecification(
        filters: List<GameFilter>,
        category: GameCategory?,
        conference: String?,
        season: Int?,
        week: Int?,
    ): Specification<Game> {
        return Specification { root: Root<Game>, _: CriteriaQuery<*>, cb: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            // Handle category filter
            category?.let {
                when (it) {
                    ONGOING -> {
                        predicates.add(cb.notEqual(root.get<Game.GameStatus>("gameStatus"), Game.GameStatus.FINAL))
                        predicates.add(cb.notEqual(root.get<Game.GameType>("gameType"), Game.GameType.SCRIMMAGE))
                    }
                    PAST -> {
                        predicates.add(cb.equal(root.get<Game.GameStatus>("gameStatus"), Game.GameStatus.FINAL))
                        predicates.add(cb.notEqual(root.get<Game.GameType>("gameType"), Game.GameType.SCRIMMAGE))
                    }
                    SCRIMMAGE -> {
                        predicates.add(cb.notEqual(root.get<Game.GameStatus>("gameStatus"), Game.GameStatus.FINAL))
                        predicates.add(cb.equal(root.get<Game.GameType>("gameType"), Game.GameType.SCRIMMAGE))
                    }
                    PAST_SCRIMMAGE -> {
                        predicates.add(cb.equal(root.get<Game.GameStatus>("gameStatus"), Game.GameStatus.FINAL))
                        predicates.add(cb.equal(root.get<Game.GameType>("gameType"), Game.GameType.SCRIMMAGE))
                    }
                }
            }

            // Handle other filters
            filters.forEach { filter ->
                when (filter) {
                    RANKED_GAME -> {
                        val rankedGames = gameRepository.getRankedGames().map { game -> game.id }
                        if (rankedGames.isNotEmpty()) {
                            predicates.add(root.get<Int>("gameId").`in`(rankedGames))
                        }
                    }
                    CONFERENCE_GAME -> {
                        predicates.add(cb.equal(root.get<Game.GameType>("gameType"), Game.GameType.CONFERENCE_GAME))
                    }
                    OUT_OF_CONFERENCE -> {
                        predicates.add(cb.equal(root.get<Game.GameType>("gameType"), Game.GameType.OUT_OF_CONFERENCE))
                    }
                    CONFERENCE_TOURNAMENT -> {
                        predicates.add(cb.equal(root.get<Game.GameType>("gameType"), Game.GameType.CONFERENCE_TOURNAMENT))
                    }
                    CONFERENCE_CHAMPIONSHIP -> {
                        predicates.add(cb.equal(root.get<Game.GameType>("gameType"), Game.GameType.CONFERENCE_CHAMPIONSHIP))
                    }
                    REGIONAL -> {
                        predicates.add(cb.equal(root.get<Game.GameType>("gameType"), Game.GameType.REGIONAL))
                    }
                    REGIONAL_ELIMINATION -> {
                        predicates.add(cb.equal(root.get<Game.GameType>("gameType"), Game.GameType.REGIONAL_ELIMINATION))
                    }
                    SUPER_REGIONAL -> {
                        predicates.add(cb.equal(root.get<Game.GameType>("gameType"), Game.GameType.SUPER_REGIONAL))
                    }
                    COLLEGE_WORLD_SERIES -> {
                        predicates.add(cb.equal(root.get<Game.GameType>("gameType"), Game.GameType.COLLEGE_WORLD_SERIES))
                    }
                    COLLEGE_WORLD_SERIES_ELIMINATION -> {
                        predicates.add(
                            cb.equal(root.get<Game.GameType>("gameType"), Game.GameType.COLLEGE_WORLD_SERIES_ELIMINATION),
                        )
                    }
                    COLLEGE_WORLD_SERIES_CHAMPIONSHIP -> {
                        predicates.add(
                            cb.equal(root.get<Game.GameType>("gameType"), Game.GameType.COLLEGE_WORLD_SERIES_CHAMPIONSHIP),
                        )
                    }
                    PREGAME -> {
                        predicates.add(cb.equal(root.get<Game.GameStatus>("gameStatus"), Game.GameStatus.PREGAME))
                    }
                    IN_PROGRESS -> {
                        predicates.add(cb.equal(root.get<Game.GameStatus>("gameStatus"), Game.GameStatus.IN_PROGRESS))
                    }
                    EXTRA_INNINGS -> {
                        predicates.add(cb.equal(root.get<Game.GameStatus>("gameStatus"), Game.GameStatus.EXTRA_INNINGS))
                    }
                }
            }

            // Conference filter
            conference?.let {
                val conferenceTeams = teamService.getTeamsInConference(it)?.map { team -> team.name }
                predicates.add(
                    cb.or(
                        root.get<String>("homeTeam").`in`(conferenceTeams),
                        root.get<String>("awayTeam").`in`(conferenceTeams),
                    ),
                )
            }

            // Season/week filters
            season?.let { predicates.add(cb.equal(root.get<Int>("season"), it)) }
            week?.let { predicates.add(cb.equal(root.get<Int>("week"), it)) }

            cb.and(*predicates.toTypedArray())
        }
    }

    /**
     * Sort games by how far they are from being done
     * @param sort
     */
    fun createSort(sort: GameSort): List<org.springframework.data.domain.Sort.Order> {
        return when (sort) {
            CLOSEST_TO_END ->
                listOf(
                    org.springframework.data.domain.Sort.Order.desc("quarter"),
                    org.springframework.data.domain.Sort.Order.asc("clock"),
                )
            MOST_TIME_REMAINING ->
                listOf(
                    org.springframework.data.domain.Sort.Order.asc("quarter"),
                    org.springframework.data.domain.Sort.Order.desc("clock"),
                )
        }
    }
}
