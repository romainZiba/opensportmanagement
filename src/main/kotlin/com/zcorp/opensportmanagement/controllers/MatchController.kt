package com.zcorp.opensportmanagement.controllers

import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.model.Event
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.model.MatchDto
import com.zcorp.opensportmanagement.model.MatchResource
import com.zcorp.opensportmanagement.repositories.*
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotNull

@RepositoryRestController
open class MatchController(private val teamMemberRepository: TeamMemberRepository,
                           private val matchRepository: MatchRepository,
                           private val championshipRepository: ChampionshipRepository,
                           private val stadiumRepository: StadiumRepository,
                           private val opponentRepository: OpponentRepository,
                           private val eventRepository: EventRepository,
                           private val accessController: AccessController) {

    @PostMapping("/championships/{championshipId}/matches")
    open fun createMatch(@NotNull @PathVariable("championshipId") championshipId: Int,
                         @RequestBody matchDto: MatchDto,
                         authentication: Authentication): ResponseEntity<MatchResource> {
        val championship = championshipRepository.findOne(championshipId) ?: throw UserForbiddenException()
        if (accessController.isTeamAdmin(authentication, championship.season.team.id)) {
            val opponentName = matchDto.opponentName
            val stadiumName = matchDto.stadiumName
            val stadium = stadiumRepository.findByName(stadiumName)
                    ?: throw EntityNotFoundException("Stadium $stadiumName does not exist")
            val opponent = opponentRepository.findByName(opponentName)
                    ?: throw EntityNotFoundException("Opponent $opponentName does not exist")
            val match = Match(matchDto.name, matchDto.description, matchDto.fromDateTime, matchDto.toDateTime,
                    stadium, opponent, championship.season.team, championship)
            val matchSaved = eventRepository.save(match)
            return ResponseEntity(MatchResource(matchSaved), HttpStatus.CREATED)
        }
        throw UserForbiddenException()
    }

    @GetMapping("/championships/{championshipId}/matches")
    open fun getMatches(@PathVariable championshipId: Int, authentication: Authentication): ResponseEntity<List<MatchResource>> {
        val championship = championshipRepository.findOne(championshipId) ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, championship.season.team.id)) {
            return ResponseEntity.ok(championship.matches.map { match -> MatchResource(match) })
        }
        throw UserForbiddenException()
    }

    @GetMapping("/matches/{matchId}")
    open fun getMatch(@PathVariable matchId: Int, authentication: Authentication): ResponseEntity<MatchResource> {
        val match = matchRepository.findOne(matchId)
        if (accessController.isUserAllowedToAccessTeam(authentication, match.championship.season.team.id)) {
            return ResponseEntity.ok(MatchResource(match))
        }
        throw UserForbiddenException()
    }

    @PutMapping("/matches/{matchId}/{present}")
    open fun participate(@NotNull @PathVariable("matchId") matchId: Int,
                         @NotNull @PathVariable("present") present: Boolean,
                         authentication: Authentication): ResponseEntity<MatchResource> {
        var match = matchRepository.findOne(matchId) ?: throw UserForbiddenException()
        val teamId = match.championship.season.team.id
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val teamMember = teamMemberRepository.findByUsername(authentication.name, teamId)
                    ?: throw EntityNotFoundException("Team member ${authentication.name} does not exist")
            match = match.parcipate(teamMember, present)
            match = matchRepository.save(match)
            return ResponseEntity.ok(MatchResource(match))
        }
        throw EntityNotFoundException("Match not found")
    }

    @RequestMapping("/matches/{matchId}", method = [RequestMethod.DELETE])
    open fun deleteChampionship(@PathVariable("matchId") matchId: Int,
                                authentication: Authentication): ResponseEntity<Any> {
        val match = matchRepository.findOne(matchId) ?: throw UserForbiddenException()
        if (accessController.isTeamAdmin(authentication, match.championship.season.team.id)) {
            matchRepository.delete(matchId)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }
}