package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.*
import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.dto.OpponentDto
import com.zcorp.opensportmanagement.dto.SeasonDto
import com.zcorp.opensportmanagement.dto.TeamDto
import com.zcorp.opensportmanagement.model.*
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import com.zcorp.opensportmanagement.repositories.SeasonRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.repositories.UserRepository
import com.zcorp.opensportmanagement.rest.resources.*
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.validation.constraints.NotNull

@RepositoryRestController
@RequestMapping("/teams")
open class TeamController @Autowired constructor(private val teamRepository: TeamRepository,
                                                 private val userRepository: UserRepository,
                                                 private val opponentRepository: OpponentRepository,
                                                 private val seasonRepository: SeasonRepository,
                                                 private val accessController: AccessController) {

    @GetMapping
    open fun getTeams(authentication: Authentication): ResponseEntity<List<TeamResource>> {
        val teams = teamRepository.findByIds(accessController.getUserTeamIds(authentication))
        return ResponseEntity.ok(teams.map { team -> TeamResource(team) })
    }

    @PostMapping
    open fun createTeam(@RequestBody teamDto: TeamDto,
                        authentication: Authentication): ResponseEntity<TeamResource> {
        var team = Team(teamDto.name, teamDto.sport, teamDto.genderKind, teamDto.ageGroup)
        val user = userRepository.findByUsername(authentication.name)
                ?: throw EntityNotFoundException("User ${authentication.name} does not exist")
        val teamMember = TeamMember(user, mutableSetOf(TeamMember.Role.ADMIN), team)
        team.members.add(teamMember)
        team = teamRepository.save(team)
        return ResponseEntity(TeamResource(team), HttpStatus.CREATED)
    }

    @GetMapping("/{teamId}")
    open fun getTeam(@PathVariable teamId: Int, authentication: Authentication): ResponseEntity<TeamResource> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val team = teamRepository.getOne(teamId)
            return ResponseEntity.ok(TeamResource(team))
        }
        throw UserForbiddenException()
    }

    @PutMapping("/{teamId}/join")
    open fun joinTeam(@PathVariable teamId: Int, authentication: Authentication): ResponseEntity<TeamResource> {
        if (accessController.getUserTeamIds(authentication).contains(teamId)) {
            throw UserAlreadyMemberException()
        }
        val team = teamRepository.getOne(teamId) ?: throw EntityNotFoundException("Team $teamId does not exist")
        val user = userRepository.findByUsername(authentication.name)
        val teamMember = TeamMember(user!!, mutableSetOf(TeamMember.Role.PLAYER), team)
        team.members.add(teamMember)
        return ResponseEntity.ok(TeamResource(teamRepository.save(team)))
    }

    @DeleteMapping("/{teamId}")
    open fun deleteTeam(@PathVariable teamId: Int, authentication: Authentication): ResponseEntity<Any> {
        if (accessController.isTeamAdmin(authentication, teamId)) {
            teamRepository.deleteById(teamId)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }

    @GetMapping("/{teamId}/events")
    open fun getEvents(@PathVariable("teamId") teamId: Int, authentication: Authentication): ResponseEntity<List<EventResource>> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val team = teamRepository.getOne(teamId)
            return ResponseEntity.ok(team.events.map { event -> EventResource(event) })
        }
        throw UserForbiddenException()
    }

    @PostMapping("/{teamId}/events")
    open fun createEvent(@PathVariable("teamId") teamId: Int,
                         @RequestBody eventDto: EventDto,
                         authentication: Authentication): ResponseEntity<Any> {
        if (accessController.isTeamAdmin(authentication, teamId)) {
            val team = teamRepository.getOne(teamId)
            val event = createEventFromDto(eventDto, team)
            team.events.add(event)
            teamRepository.save(team)
            return ResponseEntity.created(URI("")).build()
        }
        throw UserForbiddenException()
    }

    @PostMapping("/{teamId}/opponents")
    open fun createOpponent(@NotNull @PathVariable("teamId") teamId: Int,
                            @RequestBody opponentDto: OpponentDto,
                            authentication: Authentication): ResponseEntity<OpponentResource> {

        if (accessController.isTeamAdmin(authentication, teamId)) {
            val team = teamRepository.getOne(teamId) ?: throw EntityNotFoundException("Team $teamId does not exist")
            if (team.opponents.map { it.name }.contains(opponentDto.name)) {
                throw EntityAlreadyExistsException("Opponent " + opponentDto.name + " already exists")
            } else {
                var opponent = Opponent(opponentDto.name, opponentDto.phoneNumber, opponentDto.email, team)
                opponent = opponentRepository.save(opponent)
                return ResponseEntity(OpponentResource(opponent), HttpStatus.CREATED)
            }
        }
        throw UserForbiddenException()
    }

    @GetMapping("/{teamId}/opponents")
    open fun getOpponents(@PathVariable("teamId") teamId: Int, authentication: Authentication): ResponseEntity<List<OpponentResource>> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val team = teamRepository.getOne(teamId)
            return ResponseEntity.ok(team.opponents.map { opponent -> OpponentResource(opponent) })
        }
        throw UserForbiddenException()
    }

    @GetMapping("/{teamId}/seasons")
    open fun getSeasons(@PathVariable("teamId") teamId: Int,
                        authentication: Authentication): ResponseEntity<List<SeasonResource>> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val team: Team = teamRepository.getOne(teamId)
                    ?: throw EntityNotFoundException("Team $teamId does not exist")
            return ResponseEntity.ok(team.seasons.map { season -> SeasonResource(season) })
        }
        throw UserForbiddenException()
    }

    @PostMapping("/{teamId}/seasons")
    open fun createSeason(@NotNull @PathVariable("teamId") teamId: Int,
                          @RequestBody seasonDto: SeasonDto,
                          authentication: Authentication): ResponseEntity<SeasonResource> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val team: Team = teamRepository.getOne(teamId)
                    ?: throw EntityNotFoundException("Team $teamId does not exist")
            if (team.seasons.map { it.name }.contains(seasonDto.name)) {
                throw EntityAlreadyExistsException("Season " + seasonDto.name + " already exists")
            } else {
                val season = Season(seasonDto.name, seasonDto.fromDate, seasonDto.toDate, seasonDto.status, mutableSetOf(), team)
                val seasonSaved = seasonRepository.save(season)
                return ResponseEntity(SeasonResource(seasonSaved), HttpStatus.CREATED)
            }
        }
        throw UserForbiddenException()
    }

    @GetMapping("/{teamId}/stadiums")
    open fun getStadiums(@PathVariable("teamId") teamId: Int, authentication: Authentication): ResponseEntity<List<StadiumResource>> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val team = teamRepository.getOne(teamId)
            return ResponseEntity.ok(team.stadiums.map { stadium -> StadiumResource(stadium) })
        }
        throw UserForbiddenException()
    }

    @GetMapping("/{teamId}/members")
    open fun getTeamMembers(@PathVariable("teamId") teamId: Int, authentication: Authentication): ResponseEntity<List<TeamMemberResource>> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val team = teamRepository.getOne(teamId)
            return ResponseEntity.ok(team.members.map { teamMember -> TeamMemberResource(teamMember) })
        }
        throw UserForbiddenException()
    }

    private fun createEventFromDto(eventDto: EventDto, team: Team): Event {
        val place = eventDto.place
        val stadium = eventDto.stadium
        if (place == null && stadium == null) {
            throw BadInputException("Either stadium or place must be provided")
        }
        val recurrenceDays = eventDto.reccurenceDays
        val fromDateTime = eventDto.fromDateTime
        val toDateTime = eventDto.toDateTime
        val recurrenceFromTime = eventDto.recurrenceFromTime
        val recurrenceToTime = eventDto.recurrenceToTime
        if ((recurrenceDays == null || recurrenceFromTime == null || recurrenceToTime == null)
                && (fromDateTime == null || toDateTime == null)) {
            throw BadInputException("Either recurrence or fixed event information must be provided")
        }

        if (eventDto.reccurenceDays == null) {
            return if (stadium == null) {
                Event(eventDto.name, eventDto.description, fromDateTime!!, toDateTime!!,
                        place!!, team)
            } else {
                Event(eventDto.name, eventDto.description, fromDateTime!!, toDateTime!!,
                        stadium, team)
            }
        } else {
            return if (stadium == null) {
                Event(eventDto.name, eventDto.description, eventDto.reccurenceDays, eventDto.recurrenceFromDate!!,
                        eventDto.recurrenceToDate!!, recurrenceFromTime!!, recurrenceToTime!!, place!!, team)
            } else {
                Event(eventDto.name, eventDto.description, eventDto.reccurenceDays, eventDto.recurrenceFromDate!!,
                        eventDto.recurrenceToDate!!, recurrenceFromTime!!, recurrenceToTime!!, stadium, team)
            }
        }
    }

    /** Handle the error */
    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleError(e: EntityNotFoundException) = e.message

    @ExceptionHandler(EntityAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleError(e: EntityAlreadyExistsException) = e.message

    @ExceptionHandler(UserForbiddenException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleError(e: UserForbiddenException) = e.message

    @ExceptionHandler(UserAlreadyMemberException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleError(e: UserAlreadyMemberException) = e.message

}