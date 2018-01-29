package com.zcorp.opensportmanagement.controllers

import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.model.Championship
import com.zcorp.opensportmanagement.model.ChampionshipDto
import com.zcorp.opensportmanagement.model.ChampionshipResource
import com.zcorp.opensportmanagement.repositories.ChampionshipRepository
import com.zcorp.opensportmanagement.repositories.SeasonRepository
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@RepositoryRestController
open class ChampionshipController(private val championshipRepository: ChampionshipRepository,
                                  private val seasonRepository: SeasonRepository,
                                  private val accessController: AccessController) {

    @RequestMapping("/seasons/{seasonId}/championships", method = [RequestMethod.POST])
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

    @RequestMapping("/seasons/{seasonId}/championships", method = [RequestMethod.GET])
    open fun getChampionships(@PathVariable("seasonId") seasonId: Int, authentication: Authentication): ResponseEntity<List<ChampionshipResource>> {
        val season = seasonRepository.findOne(seasonId) ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, season.team.id)) {
            return ResponseEntity.ok(season.championships.map { championship -> ChampionshipResource(championship) })
        }
        throw UserForbiddenException()
    }

    @RequestMapping("/seasons/{seasonId}/championships/{championshipId}", method = [RequestMethod.GET])
    open fun getChampionship(@PathVariable("championshipId") championshipId: Int,
                             authentication: Authentication): ResponseEntity<ChampionshipResource> {
        val championship = championshipRepository.findOne(championshipId) ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, championship.season.team.id)) {
            return ResponseEntity.ok(ChampionshipResource(championship))
        }
        throw UserForbiddenException()
    }

    @RequestMapping("/seasons/{seasonId}/championships/{championshipId}", method = [RequestMethod.DELETE])
    open fun deleteChampionship(@PathVariable("championshipId") championshipId: Int,
                                authentication: Authentication): ResponseEntity<Any> {
        val championship = championshipRepository.findOne(championshipId) ?: throw UserForbiddenException()
        if (accessController.isTeamAdmin(authentication, championship.season.team.id)) {
            championshipRepository.delete(championship.id)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }
}