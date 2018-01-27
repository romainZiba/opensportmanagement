package com.zcorp.opensportmanagement.controllers

import com.zcorp.opensportmanagement.EntityAlreadyExistsException
import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.model.Season
import com.zcorp.opensportmanagement.model.SeasonDto
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.repositories.SeasonRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotNull

@RestController
open class SeasonController(private val seasonRepository: SeasonRepository,
                       private val accessController: AccessController,
                       private val teamRepository: TeamRepository) {


    @PostMapping("/teams/{teamId}/seasons")
    fun createSeason(@NotNull @PathVariable("teamId") teamId: Int,
                     @RequestBody seasonDto: SeasonDto,
                     authentication: Authentication): ResponseEntity<Season> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val team: Team = teamRepository.findOne(teamId)
                    ?: throw EntityNotFoundException("Team $teamId does not exist")
            if (team.seasons.map { it.name }.contains(seasonDto.name)) {
                throw EntityAlreadyExistsException("Season " + seasonDto.name + " already exists")
            } else {
                val season = Season(seasonDto.name, seasonDto.fromDate, seasonDto.toDate, seasonDto.status, mutableSetOf(), team)
                val seasonSaved = seasonRepository.save(season)
                return ResponseEntity(seasonSaved, HttpStatus.CREATED)
            }
        }
        throw UserForbiddenException()
    }

    @GetMapping("/teams/{teamId}/seasons")
    open fun getSeasons(@PathVariable("teamId") teamId: Int,
                   authentication: Authentication): MutableSet<Season> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val team: Team = teamRepository.findOne(teamId)
                    ?: throw EntityNotFoundException("Team $teamId does not exist")
            return team.seasons
        }
        throw UserForbiddenException()
    }

    @GetMapping("/teams/{teamId}/seasons/{seasonId}")
    fun getSeason(@PathVariable("teamId") teamId: Int,
                  @PathVariable("seasonId") seasonId: Int,
                  authentication: Authentication): Season {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val team: Team = teamRepository.findOne(teamId)
                    ?: throw EntityNotFoundException("Team $teamId does not exist")
            return team.seasons.filter { it.id == seasonId }.first()
        }
        throw UserForbiddenException()
    }

    @DeleteMapping("/teams/{teamId}/seasons/{seasonId}")
    fun deleteSeason(@PathVariable("teamId") teamId: Int,
                   @PathVariable("seasonId") seasonId: Int,
                   authentication: Authentication) {
        if (accessController.isTeamAdmin(authentication, teamId)) {
            seasonRepository.delete(seasonId)
        }
    }
}