package com.zcorp.opensportmanagement.resources


import com.zcorp.opensportmanagement.EntityAlreadyExistsException
import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.UserAlreadyMemberException
import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.model.*
import com.zcorp.opensportmanagement.repositories.*
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotNull

@RestController
class TeamController(private val teamRepository: TeamRepository,
                     private val seasonRepository: SeasonRepository,
                     private val opponentRepository: OpponentRepository,
                     private val userRepository: UserRepository,
                     private val teamMemberRepository: TeamMemberRepository,
                     private val accessController: AccessController) {


    @GetMapping("/teams")
    fun getTeams(authentication: Authentication) = teamRepository.findByIds(accessController.getUserTeamIds(authentication))

    @GetMapping("/teams/{teamId}")
    fun getTeam(@PathVariable teamId: Int, authentication: Authentication): Team {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            return teamRepository.findOne(teamId)
        }
        throw UserForbiddenException()
    }

    @PutMapping("/teams/{teamId}/join")
    fun joinTeam(@PathVariable teamId: Int, authentication: Authentication): Team {
        if (accessController.getUserTeamIds(authentication).contains(teamId)) {
            throw UserAlreadyMemberException()
        }
        val team = teamRepository.findOne(teamId) ?: throw EntityNotFoundException("Team $teamId does not exist")
        val user = userRepository.findByUsername(authentication.name)
        val teamMember = TeamMember(user!!, false, Role.PLAYER, null, team)
        team.members.add(teamMember)
        teamRepository.save(team)
        return team
    }

    @PostMapping("/teams")
    fun createTeam(@RequestBody teamDto: TeamDto): ResponseEntity<Team> {
        val team = Team(teamDto.name, teamDto.sport, teamDto.genderKind, teamDto.ageGroup)
        val teamSaved = teamRepository.save(team)
        return ResponseEntity(teamSaved, HttpStatus.CREATED)
    }


    @PostMapping("/teams/{teamId}/seasons")
    fun createSeason(@NotNull @PathVariable("teamId") teamId: Int,
                     @RequestBody seasonDto: SeasonDto,
                     authentication: Authentication): ResponseEntity<Season> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
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
        throw UserForbiddenException()
    }

    @PostMapping("/teams/{teamId}/opponents")
    fun createOpponent(@NotNull @PathVariable("teamId") teamId: Int,
                       @RequestBody opponentDto: OpponentDto,
                       authentication: Authentication): ResponseEntity<Opponent> {

        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
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
        throw UserForbiddenException()

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