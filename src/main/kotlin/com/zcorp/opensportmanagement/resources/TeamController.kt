package com.zcorp.opensportmanagement.resources

import com.zcorp.opensportmanagement.EntityAlreadyExistsException
import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.UserAlreadyMemberException
import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.model.Role
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.model.TeamDto
import com.zcorp.opensportmanagement.model.TeamMember
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.repositories.UserRepository
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.hateoas.Resource
import org.springframework.hateoas.Resources
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RepositoryRestController
open class TeamController(private val teamRepository: TeamRepository,
                          private val userRepository: UserRepository,
                          private val accessController: AccessController) {

    @RequestMapping("/teams", method = [RequestMethod.POST])
    open fun createTeam(@RequestBody teamDto: TeamDto,
                        authentication: Authentication): ResponseEntity<Resource<Team>> {
        val team = Team(teamDto.name, teamDto.sport, teamDto.genderKind, teamDto.ageGroup)
        val user = userRepository.findByUsername(authentication.name)
                ?: throw EntityNotFoundException("User ${authentication.name} does not exist")
        val teamMember = TeamMember(user, mutableSetOf(Role.ADMIN), team)
        team.members.add(teamMember)
        val teamSaved = teamRepository.save(team)
        val resource = Resource<Team>(teamSaved)
        resource.add(linkTo(methodOn(TeamController::class.java).createTeam(teamDto, authentication)).withSelfRel())
        return ResponseEntity(resource, HttpStatus.CREATED)
    }

    @RequestMapping("/teams", method = [RequestMethod.GET])
    open fun getTeams(authentication: Authentication): ResponseEntity<Resources<Team>> {
        val teams = teamRepository.findByIds(accessController.getUserTeamIds(authentication))
        val resources = Resources<Team>(teams)
        resources.add(linkTo(methodOn(TeamController::class.java).getTeams(authentication)).withSelfRel())
        return ResponseEntity.ok(resources)
    }

    @RequestMapping("/teams/{teamId}", method = [RequestMethod.GET])
    open fun getTeam(@PathVariable teamId: Int, authentication: Authentication): ResponseEntity<Resource<Team>> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            return ResponseEntity.ok(Resource(teamRepository.findOne(teamId)))
        }
        throw UserForbiddenException()
    }

    @RequestMapping("/teams/{teamId}/join", method = [RequestMethod.PUT])
    open fun joinTeam(@PathVariable teamId: Int, authentication: Authentication): ResponseEntity<Resource<Team>> {
        if (accessController.getUserTeamIds(authentication).contains(teamId)) {
            throw UserAlreadyMemberException()
        }
        val team = teamRepository.findOne(teamId) ?: throw EntityNotFoundException("Team $teamId does not exist")
        val user = userRepository.findByUsername(authentication.name)
        val teamMember = TeamMember(user!!, mutableSetOf(Role.PLAYER), team)
        team.members.add(teamMember)
        return ResponseEntity.ok(Resource(teamRepository.save(team)))
    }

    @RequestMapping("/teams/{teamId}", method = [RequestMethod.DELETE])
    open fun deleteTeam(@PathVariable teamId: Int, authentication: Authentication) {
        if (accessController.isTeamAdmin(authentication, teamId)) {
            teamRepository.delete(teamId)
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