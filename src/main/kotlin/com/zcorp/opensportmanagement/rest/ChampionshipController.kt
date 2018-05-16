package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.dto.ChampionshipDto
import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.dto.MatchCreationDto
import com.zcorp.opensportmanagement.dto.MatchDto
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.ChampionshipService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotNull

@RepositoryRestController
@RequestMapping("/championships")
open class ChampionshipController @Autowired constructor(private val championshipService: ChampionshipService,
                                                         private val accessController: AccessController) {

    @GetMapping("/{championshipId}")
    open fun getChampionship(@PathVariable("championshipId") championshipId: Int,
                             authentication: Authentication): ResponseEntity<ChampionshipDto> {
        val championshipDto = championshipService.getChampionship(championshipId)
        if (accessController.isUserAllowedToAccessTeam(authentication, championshipDto.teamId!!)) {
            return ResponseEntity.ok(championshipDto)
        }
        throw UserForbiddenException()
    }

    @DeleteMapping("/{championshipId}")
    open fun deleteChampionship(@PathVariable("championshipId") championshipId: Int,
                                authentication: Authentication): ResponseEntity<Any> {
        val championshipDto = championshipService.getChampionship(championshipId)
        if (accessController.isTeamAdmin(authentication, championshipDto.teamId!!)) {
            championshipService.deleteChampionship(championshipId)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }

    @PostMapping("/{championshipId}/matches")
    open fun createMatch(@NotNull @PathVariable("championshipId") championshipId: Int,
                         @RequestBody dto: MatchCreationDto,
                         authentication: Authentication): ResponseEntity<EventDto> {
        val championshipDto = championshipService.getChampionship(championshipId)
        if (accessController.isTeamAdmin(authentication, championshipDto.teamId!!)) {
            val match = championshipService.createMatch(dto, championshipId)
            return ResponseEntity(match.toDto(), HttpStatus.CREATED)
        }
        throw UserForbiddenException()
    }
}