package com.zcorp.opensportmanagement.resources


import com.zcorp.opensportmanagement.EntityAlreadyExistsException
import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.model.*
import com.zcorp.opensportmanagement.repositories.*
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
                     private val teamMemberRepository: TeamMemberRepository) {

    private fun getUserTeamNames(authentication: Authentication): List<String> {
        return authentication.authorities.map { it.authority }
    }

    private fun isUserAllowedToAccessTeam(authentication: Authentication, team: Team): Boolean {
        return getUserTeamNames(authentication).contains(team.name)
    }

    @GetMapping("/teams")
    fun findAll(authentication: Authentication) = teamRepository.findByNames(getUserTeamNames(authentication))

    @GetMapping("/teams/{name}")
    fun findByName(@PathVariable name: String, authentication: Authentication): Team {
        val team = teamRepository.findByName(name)
        if (team == null) {
            throw EntityNotFoundException("Team $name does not exist")
        } else {
            if (isUserAllowedToAccessTeam(authentication, team)) {
                return team
            }
            throw UserForbiddenException("User not allowed")
        }
    }

    @PutMapping("/teams/{id}/join")
    fun joinTeam(@PathVariable id: Int, authentication: Authentication): Team {
        val team = teamRepository.findOne(id)
        if (team == null) {
            throw EntityNotFoundException("Team $id does not exist")
        } else {
            val user = userRepository.findByUsername(authentication.name)
                    ?: throw EntityNotFoundException("User '$authentication.name' not found")
            var teamMember = teamMemberRepository.findByUsername(authentication.name)
            if (teamMember == null) {
                teamMember = TeamMember(user, false, Role.PLAYER, null, team)
            }
            team.members.add(teamMember)
            return team
        }
    }

    @PostMapping("/teams")
    fun createTeam(@RequestBody teamDto: TeamDto): ResponseEntity<Team> {
        if (teamRepository.findByName(teamDto.name) == null) {
            val team = Team(teamDto.name, teamDto.sport, teamDto.genderKind, teamDto.ageGroup)
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

    @ExceptionHandler(UserForbiddenException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleError(e: UserForbiddenException) = e.message
}