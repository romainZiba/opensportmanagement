package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import com.zcorp.opensportmanagement.rest.resources.OpponentResource
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@RepositoryRestController
@RequestMapping("/opponents")
open class OpponentController @Autowired constructor(private val opponentRepository: OpponentRepository,
                                                     private val accessController: AccessController) {

    @GetMapping("/{opponentId}")
    open fun getOpponent(@PathVariable("opponentId") opponentId: Int,
                         authentication: Authentication): ResponseEntity<OpponentResource> {
        val opponent = opponentRepository.findOne(opponentId) ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, opponent.team.id)) {
            return ResponseEntity.ok(OpponentResource(opponent))
        }
        throw UserForbiddenException()
    }

    @DeleteMapping("/{opponentId}")
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