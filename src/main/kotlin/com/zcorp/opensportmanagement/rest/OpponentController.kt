package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.EntityAlreadyExistsException
import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.dto.OpponentDto
import com.zcorp.opensportmanagement.model.Opponent
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.rest.resources.OpponentResource
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
                            authentication: Authentication): ResponseEntity<OpponentResource> {

        if (accessController.isTeamAdmin(authentication, teamId)) {
            val team = teamRepository.findOne(teamId) ?: throw EntityNotFoundException("Team $teamId does not exist")
            if (team.opponents.map { it.name }.contains(opponentDto.name)) {
                throw EntityAlreadyExistsException("Opponent " + opponentDto.name + " already exists")
            } else {
                var opponent = Opponent(opponentDto.name, opponentDto.phoneNumber, opponentDto.email, team)
                opponent = opponentRepository.save(opponent)
                return ResponseEntity(OpponentResource(opponent), HttpStatus.CREATED)
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

    @RequestMapping("/opponents/{opponentId}", method = [RequestMethod.GET])
    open fun getOpponent(@PathVariable("opponentId") opponentId: Int,
                         authentication: Authentication): ResponseEntity<OpponentResource> {
        val opponent = opponentRepository.findOne(opponentId) ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, opponent.team.id)) {
            return ResponseEntity.ok(OpponentResource(opponent))
        }
        throw UserForbiddenException()
    }

    @RequestMapping("/opponents/{opponentId}", method = [RequestMethod.DELETE])
    open fun deleteOpponent(@PathVariable("opponentId") opponentId: Int,
                            authentication: Authentication): ResponseEntity<Any> {
        val opponent = opponentRepository.findOne(opponentId) ?: throw UserForbiddenException()
        if (accessController.isTeamAdmin(authentication, opponent.team.id)) {
            opponentRepository.delete(opponentId)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }
}