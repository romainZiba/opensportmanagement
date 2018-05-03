package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.dto.ChampionshipDto
import com.zcorp.opensportmanagement.dto.EventDto
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
        val championship = championshipService.getChampionship(championshipId) ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, championship.season.team.id)) {
            return ResponseEntity.ok(championship.toDto())
        }
        throw UserForbiddenException()
    }

    @DeleteMapping("/{championshipId}")
    open fun deleteChampionship(@PathVariable("championshipId") championshipId: Int,
                                authentication: Authentication): ResponseEntity<Any> {
        val championship = championshipService.getChampionship(championshipId) ?: throw UserForbiddenException()
        if (accessController.isTeamAdmin(authentication, championship.season.team.id)) {
            championshipService.deleteChampionship(championship.id)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }

    //TODO
//    @GetMapping("/{championshipId}/matches")
//    open fun getMatches(@PathVariable championshipId: Int, authentication: Authentication): ResponseEntity<List<MatchResource>> {
//        val championship = championshipRepository.getOne(championshipId) ?: throw UserForbiddenException()
//        if (accessController.isUserAllowedToAccessTeam(authentication, championship.season.team.id)) {
//            return ResponseEntity.ok(championship.matches.map { match -> MatchResource(match) })
//        }
//        throw UserForbiddenException()
//    }

    @PostMapping("/{championshipId}/matches")
    open fun createMatch(@NotNull @PathVariable("championshipId") championshipId: Int,
                         @RequestBody matchDto: MatchDto,
                         authentication: Authentication): ResponseEntity<EventDto> {
        val championship = championshipService.getChampionship(championshipId) ?: throw UserForbiddenException()
        if (accessController.isTeamAdmin(authentication, championship.season.team.id)) {
            val match = championshipService.createMatch(matchDto, championshipId)
            return ResponseEntity(match.toDto(), HttpStatus.CREATED)
        }
        throw UserForbiddenException()
    }
}