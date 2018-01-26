package com.zcorp.opensportmanagement.resources

import com.zcorp.opensportmanagement.EntityAlreadyExistsException
import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.model.Opponent
import com.zcorp.opensportmanagement.model.OpponentDto
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotNull

@RestController
class OpponentController(val opponentRepository: OpponentRepository,
                         val teamRepository: TeamRepository,
                         val accessController: AccessController) {

    @PostMapping("/teams/{teamId}/opponents")
    fun createOpponent(@NotNull @PathVariable("teamId") teamId: Int,
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
}