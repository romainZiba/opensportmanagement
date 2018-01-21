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
class Controller(private val teamRepository: TeamRepository,
                 private val seasonRepository: SeasonRepository,
                 private val championshipRepository: ChampionshipRepository,
                 private val opponentRepository: OpponentRepository,
                 private val eventRepository: EventRepository,
                 private val stadiumRepository: StadiumRepository,
                 private val matchRepository: MatchRepository) {

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

    @PostMapping("/seasons/{seasonId}/championships")
    fun createChampionship(@NotNull @PathVariable("seasonId") seasonId: Int,
                           @RequestBody championshipDto: ChampionshipDto): ResponseEntity<Championship> {
        val season = seasonRepository.findOne(seasonId)
        if (season != null) {
            if (season.championships.map { it.name }.contains(championshipDto.name)) {
                throw EntityAlreadyExistsException("Championship " + championshipDto.name + " already exists")
            } else {
                val championship = Championship(championshipDto.name, season, mutableSetOf())
                val championshipSaved = championshipRepository.save(championship)
                return ResponseEntity(championshipSaved, HttpStatus.CREATED)
            }
        } else {
            throw EntityNotFoundException("Season $seasonId does not exist")
        }
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

    @PostMapping("/championships/{championshipId}/matches")
    fun createMatch(@NotNull @PathVariable("teamId") teamId: Int,
                    @NotNull @PathVariable("championshipId") championshipId: Int,
                    @RequestBody matchDto: MatchDto): ResponseEntity<Event> {
        val championship = championshipRepository.findOne(championshipId)
        if (championship != null) {
            val opponentName = matchDto.opponentName
            val stadiumName = matchDto.stadiumName
            val stadium = stadiumRepository.findByName(stadiumName) ?: throw EntityNotFoundException("Stadium $stadiumName does not exist")
            val opponent = opponentRepository.findByName(opponentName) ?: throw EntityNotFoundException("Opponent $opponentName does not exist")
            val match = Match(matchDto.name, matchDto.description, matchDto.fromDateTime, matchDto.toDateTime,
                    stadium, opponent, championship.season.team, championship)
            val matchSaved = eventRepository.save(match)
            return ResponseEntity(matchSaved, HttpStatus.CREATED)
        }
        throw EntityNotFoundException("Championship $championshipId does not exist")
    }

    @PutMapping("/matches/{matchId}/{present}")
    fun participate(@NotNull @PathVariable("matchId") matchId: Int,
                    @NotNull @PathVariable("present") present: Boolean) {
        val match = matchRepository.findOne(matchId)
        if (match != null) {
            //TODO: handle session
//            match.parcipate(, present)
        }
        throw EntityNotFoundException("")
    }

    /** Handle the error */
    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleError(e: EntityNotFoundException) = e.message

    @ExceptionHandler(EntityAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleError(e: EntityAlreadyExistsException) = e.message
}