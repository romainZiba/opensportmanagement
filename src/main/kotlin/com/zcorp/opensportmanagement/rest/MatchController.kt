package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.dto.ResultDto
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.MatchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotNull

@RepositoryRestController
@RequestMapping("/matches")
open class MatchController @Autowired constructor(private val matchService: MatchService,
                                                  private val accessController: AccessController) {

    @GetMapping("/{matchId}")
    open fun getMatch(@PathVariable matchId: Int, authentication: Authentication): ResponseEntity<EventDto> {
        val match = matchService.getMatch(matchId) ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, match.team.id)) {
            return ResponseEntity.ok(match.toDto())
        }
        throw UserForbiddenException()
    }

    @PutMapping("/{matchId}/score")
    open fun changeScore(@NotNull @PathVariable("matchId") matchId: Int,
                         @RequestBody resultDto: ResultDto,
                         authentication: Authentication): ResponseEntity<EventDto> {
        var match = matchService.getMatch(matchId) ?: throw UserForbiddenException()
        val teamId = match.team.id
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            match = matchService.changeScore(match, resultDto)
            return ResponseEntity.ok(match.toDto())
        }
        throw EntityNotFoundException("Match not found")
    }
}