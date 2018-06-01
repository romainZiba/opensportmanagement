package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.dto.EventCreationDto
import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.dto.OpponentDto
import com.zcorp.opensportmanagement.dto.PlaceDto
import com.zcorp.opensportmanagement.dto.SeasonDto
import com.zcorp.opensportmanagement.dto.TeamDto
import com.zcorp.opensportmanagement.dto.TeamMemberCreationDto
import com.zcorp.opensportmanagement.dto.TeamMemberDto
import com.zcorp.opensportmanagement.dto.TeamMemberUpdateDto
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.security.JWTUtils
import com.zcorp.opensportmanagement.security.OpenGrantedAuthority
import com.zcorp.opensportmanagement.service.EventService
import com.zcorp.opensportmanagement.service.TeamService
import com.zcorp.opensportmanagement.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedResources
import org.springframework.hateoas.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.util.LinkedList
import javax.servlet.http.HttpServletResponse
import javax.validation.constraints.NotNull

@RepositoryRestController
@RequestMapping("/teams")
open class TeamController @Autowired constructor(
    private val teamService: TeamService,
    private val eventService: EventService,
    private val accessController: AccessController,
    private val userService: UserService
) {

    @GetMapping
    open fun getTeams(authentication: Authentication): ResponseEntity<List<TeamDto>> {
        val teams = teamService.getTeams(accessController.getUserTeamIds(authentication))
        return ResponseEntity.ok(teams)
    }

    @PostMapping
    open fun createTeam(
        @RequestBody teamDto: TeamDto,
        authentication: Authentication,
        response: HttpServletResponse
    ): ResponseEntity<TeamDto> {
        val username = authentication.name
        val savedTeam = teamService.createTeam(teamDto, username)
        val authorities = userService.getTeamsAndRoles(username)
                ?.mapTo(LinkedList<GrantedAuthority>()) { OpenGrantedAuthority(it.team.id!!, it.roles) }
        val copyAuthentication = UsernamePasswordAuthenticationToken(
                authentication.principal, authentication.credentials, authorities)
        val newCookie = JWTUtils.getAccessCookie(copyAuthentication)
        response.addCookie(newCookie)
        return ResponseEntity(savedTeam, HttpStatus.CREATED)
    }

    @GetMapping("/{teamId}")
    open fun getTeam(@PathVariable teamId: Int, authentication: Authentication): ResponseEntity<TeamDto> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val teamDto = teamService.getTeam(teamId)
            return ResponseEntity.ok(teamDto)
        }
        throw UserForbiddenException()
    }

    @DeleteMapping("/{teamId}")
    open fun deleteTeam(@PathVariable teamId: Int, authentication: Authentication): ResponseEntity<Any> {
        if (accessController.isTeamAdmin(authentication, teamId)) {
            teamService.deleteTeam(teamId)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }

    @GetMapping("/{teamId}/events")
    open fun getEvents(
        @PathVariable("teamId") teamId: Int,
        pageable: Pageable,
        authentication: Authentication,
        assembler: PagedResourcesAssembler<EventDto>
    ): ResponseEntity<PagedResources<Resource<EventDto>>> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val eventsPage = teamService.getEvents(teamId, pageable)
            return ResponseEntity.ok(assembler.toResource(eventsPage))
        }
        throw UserForbiddenException()
    }

    @PostMapping("/{teamId}/events")
    open fun createEvent(
        @PathVariable("teamId") teamId: Int,
        @RequestBody eventDto: EventCreationDto,
        authentication: Authentication
    ): ResponseEntity<EventDto> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            eventService.createEvent(teamId, eventDto)
            return ResponseEntity(HttpStatus.CREATED)
        }
        throw UserForbiddenException()
    }

    @PostMapping("/{teamId}/places")
    open fun createPlace(
        @NotNull @PathVariable("teamId") teamId: Int,
        @RequestBody placeDto: PlaceDto,
        authentication: Authentication
    ): ResponseEntity<PlaceDto> {
        if (accessController.isTeamAdmin(authentication, teamId)) {
            val place = teamService.createPlace(placeDto, teamId)
            return ResponseEntity(place, HttpStatus.CREATED)
        }
        throw UserForbiddenException()
    }

    @PostMapping("/{teamId}/opponents")
    open fun createOpponent(
        @NotNull @PathVariable("teamId") teamId: Int,
        @RequestBody opponentDto: OpponentDto,
        authentication: Authentication
    ): ResponseEntity<OpponentDto> {

        if (accessController.isTeamAdmin(authentication, teamId)) {
            val opponent = teamService.createOpponent(opponentDto, teamId)
            return ResponseEntity(opponent, HttpStatus.CREATED)
        }
        throw UserForbiddenException()
    }

    @GetMapping("/{teamId}/opponents")
    open fun getOpponents(@PathVariable("teamId") teamId: Int, authentication: Authentication): ResponseEntity<List<OpponentDto>> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val opponents = teamService.getOpponents(teamId)
            return ResponseEntity.ok(opponents)
        }
        throw UserForbiddenException()
    }

    @GetMapping("/{teamId}/seasons")
    open fun getSeasons(
        @PathVariable("teamId") teamId: Int,
        authentication: Authentication
    ): ResponseEntity<List<SeasonDto>> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val seasons = teamService.getSeasons(teamId)
            return ResponseEntity.ok(seasons)
        }
        throw UserForbiddenException()
    }

    @PostMapping("/{teamId}/seasons")
    open fun createSeason(
        @NotNull @PathVariable("teamId") teamId: Int,
        @RequestBody seasonDto: SeasonDto,
        authentication: Authentication
    ): ResponseEntity<SeasonDto> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val season = teamService.createSeason(seasonDto, teamId)
            return ResponseEntity(season, HttpStatus.CREATED)
        }
        throw UserForbiddenException()
    }

    @GetMapping("/{teamId}/places")
    open fun getPlaces(
        @PathVariable("teamId") teamId: Int,
        authentication: Authentication
    ): ResponseEntity<List<PlaceDto>> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val places = teamService.getPlaces(teamId)
            return ResponseEntity.ok(places)
        }
        throw UserForbiddenException()
    }

    @GetMapping("/{teamId}/members")
    open fun getTeamMembers(@PathVariable("teamId") teamId: Int, authentication: Authentication): ResponseEntity<List<TeamMemberDto>> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            return ResponseEntity.ok(teamService.getTeamMembers(teamId))
        }
        throw UserForbiddenException()
    }

    @GetMapping("/{teamId}/members/me")
    open fun getMeAsTeamMember(
        @PathVariable("teamId") teamId: Int,
        authentication: Authentication
    ): ResponseEntity<TeamMemberDto> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val teamMemberDto = teamService.getTeamMemberByUsername(teamId, authentication.name) ?: throw UserForbiddenException()
            return ResponseEntity.ok(teamMemberDto)
        }
        throw UserForbiddenException()
    }

    @PutMapping("/{teamId}/members/me")
    open fun updateUserInformation(
        @PathVariable("teamId") teamId: Int,
        @RequestBody dto: TeamMemberUpdateDto,
        authentication: Authentication
    ): ResponseEntity<TeamMemberDto> {
        val teamMemberDto = teamService.updateProfile(dto, teamId, authentication.name)
        return ResponseEntity.ok(teamMemberDto)
    }

    @GetMapping("/{teamId}/members/{memberId}")
    open fun getTeamMember(
        @PathVariable("teamId") teamId: Int,
        @PathVariable("memberId") memberId: Int,
        authentication: Authentication
    ): ResponseEntity<TeamMemberDto> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val teamMember = teamService.getTeamMember(teamId, memberId) ?: throw UserForbiddenException()
            return ResponseEntity.ok(teamMember)
        }
        throw UserForbiddenException()
    }

    @PostMapping("/{teamId}/members")
    open fun createTeamMember(
            @PathVariable("teamId") teamId: Int,
            @RequestBody dto: TeamMemberCreationDto,
            authentication: Authentication
    ): ResponseEntity<TeamMemberDto> {
        if (accessController.isTeamAdmin(authentication, teamId)) {
            val savedTeamMember = teamService.createTeamMember(dto, teamId)
            return ResponseEntity.ok(savedTeamMember)
        }
        throw UserForbiddenException()
    }
}