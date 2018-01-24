package com.zcorp.opensportmanagement.resources


import com.zcorp.opensportmanagement.EntityAlreadyExistsException
import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.model.*
import com.zcorp.opensportmanagement.repositories.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotNull

@RestController
class TeamController(private val teamRepository: TeamRepository,
                 private val seasonRepository: SeasonRepository,
                 private val opponentRepository: OpponentRepository) {

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
    fun createTeam(@RequestBody teamDto: TeamDto): ResponseEntity<Team> {
        if (teamRepository.findByName(teamDto.name) == null) {
            val team = Team(teamDto.name, teamDto.sport, teamDto.genderKind, teamDto.ageGroup, mutableSetOf(),
                    mutableSetOf(), mutableSetOf(), mutableSetOf(), mutableSetOf())
            val teamSaved = teamRepository.save(team)
            return ResponseEntity(teamSaved, HttpStatus.CREATED)
        }
        throw EntityAlreadyExistsException("Team " + teamDto.name + " already exists")
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

    @PostMapping("/teams/{teamId}/opponents")
    fun createOpponent(@NotNull @PathVariable("teamId") teamId: Int,
                       @RequestBody opponentDto: OpponentDto): ResponseEntity<Opponent> {
        val team = teamRepository.findOne(teamId)
        if (team != null) {
            if (team.opponents.map { it.name }.contains(opponentDto.name)) {
                throw EntityAlreadyExistsException("Opponent " + opponentDto.name + " already exists")
            } else {
                val opponent = Opponent(opponentDto.name, opponentDto.phoneNumber, opponentDto.email, team)
                val opponentSaved = opponentRepository.save(opponent)
                return ResponseEntity(opponentSaved, HttpStatus.CREATED)
            }
        }
        throw EntityNotFoundException("Team $teamId does not exist")
    }

    /** Handle the error */
    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleError(e: EntityNotFoundException) = e.message

    @ExceptionHandler(EntityAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleError(e: EntityAlreadyExistsException) = e.message
}