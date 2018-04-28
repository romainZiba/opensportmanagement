package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.repositories.MatchRepository
import com.zcorp.opensportmanagement.repositories.TeamMemberRepository
import com.zcorp.opensportmanagement.rest.resources.MatchResource
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotNull

@RepositoryRestController
@RequestMapping("/matches")
open class MatchController @Autowired constructor(private val teamMemberRepository: TeamMemberRepository,
                                                  private val matchRepository: MatchRepository,
                                                  private val accessController: AccessController) {

    @GetMapping("/{matchId}")
    open fun getMatch(@PathVariable matchId: Int, authentication: Authentication): ResponseEntity<MatchResource> {
        val match = matchRepository.getOne(matchId)
        if (accessController.isUserAllowedToAccessTeam(authentication, match.championship.season.team.id)) {
            return ResponseEntity.ok(MatchResource(match))
        }
        throw UserForbiddenException()
    }

    @PutMapping("/{matchId}/{present}")
    open fun participate(@NotNull @PathVariable("matchId") matchId: Int,
                         @NotNull @PathVariable("present") present: Boolean,
                         authentication: Authentication): ResponseEntity<MatchResource> {
        var match = matchRepository.getOne(matchId) ?: throw UserForbiddenException()
        val teamId = match.championship.season.team.id
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val teamMember = teamMemberRepository.findByUsername(authentication.name, teamId)
                    ?: throw EntityNotFoundException("Team member ${authentication.name} does not exist")
            match.parcipate(teamMember, present)
            match = matchRepository.save(match)
            return ResponseEntity.ok(MatchResource(match))
        }
        throw EntityNotFoundException("Match not found")
    }

    @DeleteMapping("/{matchId}")
    open fun deleteChampionship(@PathVariable("matchId") matchId: Int,
                                authentication: Authentication): ResponseEntity<Any> {
        val match = matchRepository.getOne(matchId) ?: throw UserForbiddenException()
        if (accessController.isTeamAdmin(authentication, match.championship.season.team.id)) {
            matchRepository.deleteById(matchId)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }
}