package com.zcorp.opensportmanagement.resources


import com.zcorp.opensportmanagement.EntityAlreadyExistsException
import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.model.*
import com.zcorp.opensportmanagement.repositories.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotNull
import javax.websocket.server.PathParam

@RestController
class TeamController(private val teamRepository: TeamRepository,
                     private val seasonRepository: SeasonRepository,
                     private val championshipRepository: ChampionshipRepository,
                     private val opponentRepository: OpponentRepository,
                     private val eventRepository: EventRepository) {

    @GetMapping("/teams")
    fun findAll() = teamRepository.findAll()

    @GetMapping("/teams/{name}")
    fun findByName(@PathVariable name: String): Team {
        val team = teamRepository.findByName(name)
        if (team == null) {
            throw EntityNotFoundException("Team $name does not exist")
        } else {
            return team
        }
    }

    @PostMapping("/teams")
    fun createTeam(@RequestBody team: Team): ResponseEntity<Team> {
        if (teamRepository.findByName(team.name) == null) {
            var teamSaved = teamRepository.save(team)
            return ResponseEntity(teamSaved, HttpStatus.CREATED)
        }
        throw EntityAlreadyExistsException("Team " + team.name + " already exists")
    }

    @PostMapping("/teams/{teamId}/seasons")
    fun createSeason(@NotNull @PathVariable("teamId") teamId: Int,
                     @RequestBody seasonDto: SeasonDto): ResponseEntity<Season> {
        val team: Team? = teamRepository.findOne(teamId)
        if (team != null) {
            if (team.seasons.map { it.name }.contains(seasonDto.name)) {
                throw EntityAlreadyExistsException("Season " + seasonDto.name + " already exists")
            } else {
                val season = Season(seasonDto.name, seasonDto.fromDate, seasonDto.toDate, seasonDto.status, mutableSetOf(), team)
                val seasonSaved = seasonRepository.save(season)
                return ResponseEntity(seasonSaved, HttpStatus.CREATED)
            }
        }
        throw EntityNotFoundException("Team $teamId does not exist")
    }
//
//    @PostMapping("/teams/{teamId}/seasons/{seasonId}/championships")
//    fun createChampionship(@NotNull @PathParam("teamId") teamId: Int,
//                           @NotNull @PathParam("seasonId") seasonId: Int,
//                           @RequestBody championship: Championship): ResponseEntity<Championship> {
//        val team = teamRepository.findOne(teamId)
//        if (team != null) {
//            val season = seasonRepository.findOne(seasonId)
//            if (season != null) {
//                if (championshipRepository.findByName(championship.name) == null) {
//                    season.championships.add(championship)
//                    teamRepository.save(team)
//                    return ResponseEntity(championshipSaved, HttpStatus.CREATED)
//                } else {
//                    throw EntityAlreadyExistsException("Championship " + championship.name + " already exists")
//                }
//            } else {
//                throw EntityNotFoundException("Season $seasonId does not exist")
//            }
//        }
//        throw EntityNotFoundException("Team $teamId does not exist")
//    }
//
//    @PostMapping("/teams/{teamId}/opponents")
//    fun createOpponent(@NotNull @PathParam("teamId") teamId: Int,
//                       @RequestBody opponent: Opponent): ResponseEntity<Opponent> {
//        if (teamRepository.findOne(teamId) != null) {
//            if (opponentRepository.findByName(opponent.name) == null) {
//                var opponentSaved = opponentRepository.save(opponent)
//                return ResponseEntity(opponentSaved, HttpStatus.CREATED)
//            } else {
//                throw EntityAlreadyExistsException("Opponent " + opponent.name + " already exists")
//            }
//        }
//        throw EntityNotFoundException("Team $teamId does not exist")
//    }
//
//    @PostMapping("/teams/{teamId}/seasons/{seasonId}/championships/{championshipId}/events")
//    fun createChampionshipEvent(@NotNull @PathParam("teamId") teamId: Int,
//                                @NotNull @PathParam("seasonId") seasonId: Int,
//                                @NotNull @PathParam("championshipId") championshipId: Int,
//                                @RequestBody event: Event): ResponseEntity<Event> {
//        if (teamRepository.findOne(teamId) != null) {
//            if (seasonRepository.findOne(seasonId) != null) {
//                if (championshipRepository.findOne(championshipId) != null) {
//                    var eventSaved = eventRepository.save(event)
//                    return ResponseEntity(eventSaved, HttpStatus.CREATED)
//                } else {
//                    throw EntityNotFoundException("Championship $championshipId does not exist")
//                }
//            } else {
//                throw EntityNotFoundException("Season $seasonId does not exist")
//            }
//        }
//        throw EntityNotFoundException("Team $teamId does not exist")
//    }

    /** Handle the error */
    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleError(e: EntityNotFoundException) = e.message

    @ExceptionHandler(EntityAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleError(e: EntityAlreadyExistsException) = e.message
}