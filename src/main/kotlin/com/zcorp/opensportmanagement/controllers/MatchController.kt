package com.zcorp.opensportmanagement.controllers

import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.model.Event
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.model.MatchDto
import com.zcorp.opensportmanagement.repositories.*
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.security.IAuthenticationFacade
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotNull

@RestController
class MatchController(private val authenticationFacade: IAuthenticationFacade,
                      private val userRepository: UserRepository,
                      private val matchRepository: MatchRepository,
                      private val championshipRepository: ChampionshipRepository,
                      private val stadiumRepository: StadiumRepository,
                      private val opponentRepository: OpponentRepository,
                      private val eventRepository: EventRepository,
                      private val accessController: AccessController) {

    @PostMapping("/teams/{teamId}/seasons/{seasonId}/championships/{championshipId}/matches")
    fun createMatch(@PathVariable("teamId") teamId: Int,
                    @NotNull @PathVariable("championshipId") championshipId: Int,
                    @RequestBody matchDto: MatchDto,
                    authentication: Authentication): ResponseEntity<Event> {

        if (accessController.isTeamAdmin(authentication, teamId)) {
            val championship = championshipRepository.findOne(championshipId)
            if (championship != null) {
                val opponentName = matchDto.opponentName
                val stadiumName = matchDto.stadiumName
                val stadium = stadiumRepository.findByName(stadiumName)
                        ?: throw EntityNotFoundException("Stadium $stadiumName does not exist")
                val opponent = opponentRepository.findByName(opponentName)
                        ?: throw EntityNotFoundException("Opponent $opponentName does not exist")
                val match = Match(matchDto.name, matchDto.description, matchDto.fromDateTime, matchDto.toDateTime,
                        stadium, opponent, championship.season.team, championship)
                val matchSaved = eventRepository.save(match)
                return ResponseEntity(matchSaved, HttpStatus.CREATED)
            }
            throw EntityNotFoundException("Championship $championshipId does not exist")
        }
        throw UserForbiddenException()
    }

    @GetMapping("/teams/{teamId}/matches")
    fun findAll(@PathVariable teamId: Int, authentication: Authentication) {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) matchRepository.findAll()
    }

    @PutMapping("/matches/{matchId}/{present}")
    fun participate(@NotNull @PathVariable("matchId") matchId: Int,
                    @NotNull @PathVariable("present") present: Boolean): Match {

        //TODO: handle access rights
        var match = matchRepository.findOne(matchId)
        if (match != null) {
            val authentication = authenticationFacade.authentication
            val user = userRepository.findByUsername(authentication.name)
            match = match.parcipate(user!!, present)
            matchRepository.save(match)
            return match
        }
        throw EntityNotFoundException("Match not found")
    }
}