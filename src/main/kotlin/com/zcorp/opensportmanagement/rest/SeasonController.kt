package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.dto.ChampionshipDto
import com.zcorp.opensportmanagement.model.Championship
import com.zcorp.opensportmanagement.repositories.ChampionshipRepository
import com.zcorp.opensportmanagement.repositories.SeasonRepository
import com.zcorp.opensportmanagement.rest.resources.ChampionshipResource
import com.zcorp.opensportmanagement.rest.resources.SeasonResource
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RepositoryRestController
@RequestMapping("/seasons")
open class SeasonController @Autowired constructor(private val seasonRepository: SeasonRepository,
                                                   private val championshipRepository: ChampionshipRepository,
                                                   private val accessController: AccessController) {

    @GetMapping("/{seasonId}")
    fun getSeason(@PathVariable("seasonId") seasonId: Int,
                  authentication: Authentication): SeasonResource {
        val season = seasonRepository.findOne(seasonId) ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, season.team.id)) {
            return SeasonResource(season)
        }
        throw UserForbiddenException()
    }

    @DeleteMapping("/{seasonId}")
    fun deleteSeason(@PathVariable("seasonId") seasonId: Int,
                     authentication: Authentication): ResponseEntity<Any> {
        val season = seasonRepository.findOne(seasonId) ?: throw UserForbiddenException()
        if (accessController.isTeamAdmin(authentication, season.team.id)) {
            seasonRepository.delete(seasonId)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }

    @GetMapping("/{seasonId}/championships")
    open fun getChampionships(@PathVariable("seasonId") seasonId: Int, authentication: Authentication): ResponseEntity<List<ChampionshipResource>> {
        val season = seasonRepository.findOne(seasonId) ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, season.team.id)) {
            return ResponseEntity.ok(season.championships.map { championship -> ChampionshipResource(championship) })
        }
        throw UserForbiddenException()
    }

    @PostMapping("/{seasonId}/championships")
    open fun createChampionship(@PathVariable("seasonId") seasonId: Int,
                                @RequestBody championshipDto: ChampionshipDto,
                                authentication: Authentication): ResponseEntity<ChampionshipResource> {
        val season = seasonRepository.findOne(seasonId) ?: throw UserForbiddenException()
        val teamId = season.team.id
        if (accessController.isTeamAdmin(authentication, teamId)) {
            var championship = Championship(championshipDto.name, season)
            championship = championshipRepository.save(championship)
            return ResponseEntity(ChampionshipResource(championship), HttpStatus.CREATED)
        }
        throw UserForbiddenException()
    }
}