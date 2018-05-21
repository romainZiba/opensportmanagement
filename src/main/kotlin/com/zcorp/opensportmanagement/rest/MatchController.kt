package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.dto.ResultDto
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.MatchService
import com.zcorp.opensportmanagement.service.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.constraints.NotNull

@RepositoryRestController
@RequestMapping("/matches")
open class MatchController @Autowired constructor(
    private val matchService: MatchService,
    private val accessController: AccessController
) {

    @GetMapping("/{matchId}")
    open fun getMatch(@PathVariable matchId: Int, authentication: Authentication): ResponseEntity<EventDto> {
        val matchDto = matchService.getMatch(matchId)
        if (accessController.isUserAllowedToAccessTeam(authentication, matchDto.teamId!!)) {
            return ResponseEntity.ok(matchDto)
        }
        throw UserForbiddenException()
    }

    @PutMapping("/{matchId}/score")
    open fun changeScore(
        @NotNull @PathVariable("matchId") matchId: Int,
        @RequestBody resultDto: ResultDto,
        authentication: Authentication
    ): ResponseEntity<EventDto> {
        var matchDto = matchService.getMatch(matchId)
        if (accessController.isUserAllowedToAccessTeam(authentication, matchDto.teamId!!)) {
            matchDto = matchService.changeScore(matchId, resultDto)
            return ResponseEntity.ok(matchDto)
        }
        throw NotFoundException("Match not found")
    }
}