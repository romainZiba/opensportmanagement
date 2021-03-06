package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.dto.OpponentDto
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.OpponentService
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
open class OpponentController @Autowired constructor(
    private val opponentService: OpponentService,
    private val accessController: AccessController
) {

    @GetMapping("/{opponentId}")
    open fun getOpponent(
        @PathVariable("opponentId") opponentId: Int,
        authentication: Authentication
    ): ResponseEntity<OpponentDto> {
        val opponentDto = opponentService.getOpponent(opponentId)
        if (accessController.isAccountAllowedToAccessTeam(authentication, opponentDto.teamId!!)) {
            return ResponseEntity.ok(opponentDto)
        }
        throw UserForbiddenException()
    }

    @DeleteMapping("/{opponentId}")
    open fun deleteOpponent(
        @PathVariable("opponentId") opponentId: Int,
        authentication: Authentication
    ): ResponseEntity<Any> {
        val opponentDto = opponentService.getOpponent(opponentId)
        if (accessController.isTeamAdmin(authentication, opponentDto.teamId!!)) {
            opponentService.deleteOpponent(opponentId)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }
}