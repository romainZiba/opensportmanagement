package com.zcorp.opensportmanagement.resources

import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.model.Event
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.model.MatchDto
import com.zcorp.opensportmanagement.repositories.ChampionshipRepository
import com.zcorp.opensportmanagement.repositories.EventRepository
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import com.zcorp.opensportmanagement.repositories.StadiumRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotNull

@RestController
class ChampionshipController(private val championshipRepository: ChampionshipRepository,
                             private val opponentRepository: OpponentRepository,
                             private val eventRepository: EventRepository,
                             private val stadiumRepository: StadiumRepository) {

    @GetMapping("/championships")
    fun findAll() = championshipRepository.findAll()

    @PostMapping("/championships/{championshipId}/matches")
    fun createMatch(@NotNull @PathVariable("championshipId") championshipId: Int,
                    @RequestBody matchDto: MatchDto): ResponseEntity<Event> {
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
}