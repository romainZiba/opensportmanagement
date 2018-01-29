package com.zcorp.opensportmanagement.controllers

import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.model.StadiumResource
import com.zcorp.opensportmanagement.repositories.StadiumRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@RepositoryRestController
open class StadiumController(private val teamRepository: TeamRepository,
                             private val stadiumRepository: StadiumRepository,
                             private val accessController: AccessController) {

    @RequestMapping("/teams/{teamId}/stadiums", method = [RequestMethod.GET])
    open fun getStadiums(@PathVariable("teamId") teamId: Int, authentication: Authentication): ResponseEntity<List<StadiumResource>> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val team = teamRepository.findOne(teamId)
            return ResponseEntity.ok(team.stadiums.map { stadium -> StadiumResource(stadium) })
        }
        throw UserForbiddenException()
    }

    @RequestMapping("/stadiums/{stadiumId}", method = [RequestMethod.GET])
    open fun getStadium(@PathVariable("stadiumId") stadiumId: Int,
                        authentication: Authentication): ResponseEntity<StadiumResource> {
        val stadium = stadiumRepository.findOne(stadiumId) ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, stadium.team.id)) {
            return ResponseEntity.ok(StadiumResource(stadium))
        }
        throw UserForbiddenException()
    }
}
