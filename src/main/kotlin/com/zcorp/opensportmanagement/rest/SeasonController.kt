package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.EntityAlreadyExistsException
import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.dto.SeasonDto
import com.zcorp.opensportmanagement.model.Season
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.repositories.SeasonRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.rest.resources.SeasonResource
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotNull

@RepositoryRestController
open class SeasonController(private val seasonRepository: SeasonRepository,
                            private val accessController: AccessController,
                            private val teamRepository: TeamRepository) {


    @PostMapping("/teams/{teamId}/seasons")
    open fun createSeason(@NotNull @PathVariable("teamId") teamId: Int,
                          @RequestBody seasonDto: SeasonDto,
                          authentication: Authentication): ResponseEntity<SeasonResource> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val team: Team = teamRepository.findOne(teamId)
                    ?: throw EntityNotFoundException("Team $teamId does not exist")
            if (team.seasons.map { it.name }.contains(seasonDto.name)) {
                throw EntityAlreadyExistsException("Season " + seasonDto.name + " already exists")
            } else {
                val season = Season(seasonDto.name, seasonDto.fromDate, seasonDto.toDate, seasonDto.status, mutableSetOf(), team)
                val seasonSaved = seasonRepository.save(season)
                return ResponseEntity(SeasonResource(seasonSaved), HttpStatus.CREATED)
            }
        }
        throw UserForbiddenException()
    }

    @GetMapping("/teams/{teamId}/seasons")
    open fun getSeasons(@PathVariable("teamId") teamId: Int,
                        authentication: Authentication): ResponseEntity<List<SeasonResource>> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val team: Team = teamRepository.findOne(teamId)
                    ?: throw EntityNotFoundException("Team $teamId does not exist")
            return ResponseEntity.ok(team.seasons.map { season -> SeasonResource(season) })
        }
        throw UserForbiddenException()
    }

    @GetMapping("/seasons/{seasonId}")
    fun getSeason(@PathVariable("seasonId") seasonId: Int,
                  authentication: Authentication): SeasonResource {
        val season = seasonRepository.findOne(seasonId) ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, season.team.id)) {
            return SeasonResource(season)
        }
        throw UserForbiddenException()
    }

    @DeleteMapping("/seasons/{seasonId}")
    fun deleteSeason(@PathVariable("seasonId") seasonId: Int,
                     authentication: Authentication): ResponseEntity<Any> {
        val season = seasonRepository.findOne(seasonId) ?: throw UserForbiddenException()
        if (accessController.isTeamAdmin(authentication, season.team.id)) {
            seasonRepository.delete(seasonId)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }
}