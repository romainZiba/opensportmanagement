package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.dto.MatchDto
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.repositories.ChampionshipRepository
import com.zcorp.opensportmanagement.repositories.MatchRepository
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import com.zcorp.opensportmanagement.repositories.StadiumRepository
import com.zcorp.opensportmanagement.rest.resources.ChampionshipResource
import com.zcorp.opensportmanagement.rest.resources.MatchResource
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotNull

@RepositoryRestController
@RequestMapping("/championships")
open class ChampionshipController @Autowired constructor(private val championshipRepository: ChampionshipRepository,
                                                         private val matchRepository: MatchRepository,
                                                         private val stadiumRepository: StadiumRepository,
                                                         private val opponentRepository: OpponentRepository,
                                                         private val accessController: AccessController) {

    @GetMapping("/{championshipId}")
    open fun getChampionship(@PathVariable("championshipId") championshipId: Int,
                             authentication: Authentication): ResponseEntity<ChampionshipResource> {
        val championship = championshipRepository.getOne(championshipId) ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, championship.season.team.id)) {
            return ResponseEntity.ok(ChampionshipResource(championship))
        }
        throw UserForbiddenException()
    }

    @DeleteMapping("/{championshipId}")
    open fun deleteChampionship(@PathVariable("championshipId") championshipId: Int,
                                authentication: Authentication): ResponseEntity<Any> {
        val championship = championshipRepository.getOne(championshipId) ?: throw UserForbiddenException()
        if (accessController.isTeamAdmin(authentication, championship.season.team.id)) {
            championshipRepository.deleteById(championship.id)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }

    @GetMapping("/{championshipId}/matches")
    open fun getMatches(@PathVariable championshipId: Int, authentication: Authentication): ResponseEntity<List<MatchResource>> {
        val championship = championshipRepository.getOne(championshipId) ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, championship.season.team.id)) {
            return ResponseEntity.ok(championship.matches.map { match -> MatchResource(match) })
        }
        throw UserForbiddenException()
    }

    @PostMapping("/{championshipId}/matches")
    open fun createMatch(@NotNull @PathVariable("championshipId") championshipId: Int,
                         @RequestBody matchDto: MatchDto,
                         authentication: Authentication): ResponseEntity<MatchResource> {
        val championship = championshipRepository.getOne(championshipId) ?: throw UserForbiddenException()
        if (accessController.isTeamAdmin(authentication, championship.season.team.id)) {
            val opponentName = matchDto.opponentName
            val stadiumName = matchDto.stadiumName
            val stadium = stadiumRepository.findByName(stadiumName)
                    ?: throw EntityNotFoundException("Stadium $stadiumName does not exist")
            val opponent = opponentRepository.findByName(opponentName)
                    ?: throw EntityNotFoundException("Opponent $opponentName does not exist")
            val match = Match(matchDto.name, matchDto.description, matchDto.fromDateTime, matchDto.toDateTime,
                    stadium, opponent, championship.season.team, championship)
            val matchSaved = matchRepository.save(match)
            return ResponseEntity(MatchResource(matchSaved), HttpStatus.CREATED)
        }
        throw UserForbiddenException()
    }
}