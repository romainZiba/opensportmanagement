package com.zcorp.opensportmanagement.controllers

import com.zcorp.opensportmanagement.EntityAlreadyExistsException
import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.model.Opponent
import com.zcorp.opensportmanagement.model.OpponentDto
import com.zcorp.opensportmanagement.model.OpponentResource
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotNull

@RepositoryRestController
open class OpponentController(private val opponentRepository: OpponentRepository,
                              private val teamRepository: TeamRepository,
                              private val accessController: AccessController) {

    @PostMapping("/teams/{teamId}/opponents")
    open fun createOpponent(@NotNull @PathVariable("teamId") teamId: Int,
                            @RequestBody opponentDto: OpponentDto,
                            authentication: Authentication): ResponseEntity<Opponent> {

        if (accessController.isTeamAdmin(authentication, teamId)) {
            val team = teamRepository.findOne(teamId) ?: throw EntityNotFoundException("Team $teamId does not exist")
            if (team.opponents.map { it.name }.contains(opponentDto.name)) {
                throw EntityAlreadyExistsException("Opponent " + opponentDto.name + " already exists")
            } else {
                val opponent = Opponent(opponentDto.name, opponentDto.phoneNumber, opponentDto.email, team)
                val opponentSaved = opponentRepository.save(opponent)
                return ResponseEntity(opponentSaved, HttpStatus.CREATED)
            }
        }
        throw UserForbiddenException()
    }

    @RequestMapping("/teams/{teamId}/opponents", method = [RequestMethod.GET])
    open fun getOpponents(@PathVariable("teamId") teamId: Int, authentication: Authentication): ResponseEntity<List<OpponentResource>> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val team = teamRepository.findOne(teamId)
            return ResponseEntity.ok(team.opponents.map { opponent -> OpponentResource(opponent) })
        }
        throw UserForbiddenException()
    }

    @RequestMapping("/teams/{teamId}/opponents/{opponentId}", method = [RequestMethod.GET])
    open fun getStadium(@PathVariable("teamId") teamId: Int,
                        @PathVariable("opponentId") opponentId: Int,
                        authentication: Authentication): ResponseEntity<OpponentResource> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val opponent = opponentRepository.findOne(opponentId)
            return ResponseEntity.ok(OpponentResource(opponent))
        }
        throw UserForbiddenException()
    }
}