package com.fcb.porygon.controllers

import com.fcb.porygon.domain.Team
import com.fcb.porygon.service.fcfb.TeamService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("/team")
class TeamController(
    private var teamService: TeamService,
) {
    /**
     * Get a team by id
     * @param id
     */
    @GetMapping("/id")
    fun getTeamById(
        @RequestParam id: Int,
    ) = teamService.getTeamById(id)

    /**
     * Get all teams
     * @return
     */
    @GetMapping("")
    fun getAllTeams() = teamService.getAllTeams()

    /**
     * Get a team by name
     * @param name
     * @return
     */
    @GetMapping("/name")
    fun getTeamByName(
        @RequestParam name: String?,
    ) = teamService.getTeamByName(name)

    /**
     * Create a team
     * @param team
     */
    @PostMapping("")
    fun createTeam(
        @RequestBody team: Team,
    ) = teamService.createTeam(team)

    /**
     * Update a team
     * @param team
     */
    @PutMapping("")
    fun updateTeam(
        @RequestBody team: Team,
    ) = teamService.updateTeam(team)

    /**
     * Hire a coach for a team
     * @param team
     * @param discordId
     */
    @PostMapping("/hire")
    suspend fun hireCoach(
        @RequestParam team: String?,
        @RequestParam discordId: String,
    ) = teamService.hireCoach(team, discordId)

    /**
     * Hire an interim coach for a team
     * @param team
     * @param discordId
     * @param processedBy
     */
    @PostMapping("/hire/interim")
    suspend fun hireInterimCoach(
        @RequestParam team: String,
        @RequestParam discordId: String,
        @RequestParam processedBy: String,
    ) = teamService.hireInterimCoach(team, discordId, processedBy)

    /**
     * Fire all coaches for a team
     * @param team
     */
    @PostMapping("/fire")
    fun fireCoach(
        @RequestParam team: String,
    ) = teamService.fireCoach(team)

    /**
     * Get open teams
     */
    @GetMapping("/open")
    fun getOpenTeams() = teamService.getOpenTeams()

    /**
     * Delete a team
     */
    @DeleteMapping("")
    fun deleteTeam(
        @RequestParam id: Int,
    ) = teamService.deleteTeam(id)
}
