package com.zcorp.opensportmanagement.resources


import com.zcorp.opensportmanagement.EntityAlreadyExistsException
import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.model.TeamDto
import com.zcorp.opensportmanagement.services.TeamRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class TeamController(private val teamRepository: TeamRepository) {

    @GetMapping("/teams")
    fun findAll() = teamRepository.findAll().map { it.toDto() }.toList()

    @GetMapping("/teams/{name}")
    fun findByName(@PathVariable name: String): TeamDto {
        val team = teamRepository.findByName(name)
        if (team == null) {
            throw EntityNotFoundException("Team $name does not exist")
        } else {
            return team.toDto()
        }
    }

    @PostMapping("/teams")
    fun createTeam(@RequestBody team: Team): Team {
        if (teamRepository.findByName(team.name) == null) {
            return teamRepository.save(team)
        }
        throw EntityAlreadyExistsException("Team " + team.name + " already exists")
    }

    /** Handle the error */
    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleError(e: EntityNotFoundException) = e.message

    @ExceptionHandler(EntityAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleError(e: EntityAlreadyExistsException) = e.message
}