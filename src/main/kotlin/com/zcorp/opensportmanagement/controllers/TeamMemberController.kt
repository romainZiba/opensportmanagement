package com.zcorp.opensportmanagement.controllers

import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.model.TeamMemberResource
import com.zcorp.opensportmanagement.repositories.TeamMemberRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@RepositoryRestController
open class TeamMemberController(private val teamRepository: TeamRepository,
                                private val teamMemberRepository: TeamMemberRepository,
                                private val accessController: AccessController) {

    @RequestMapping("/teams/{teamId}/members", method = [RequestMethod.GET])
    open fun getTeamMembers(@PathVariable("teamId") teamId: Int, authentication: Authentication): ResponseEntity<List<TeamMemberResource>> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val team = teamRepository.findOne(teamId)
            return ResponseEntity.ok(team.members.map { teamMember -> TeamMemberResource(teamMember) })
        }
        throw UserForbiddenException()
    }

    @RequestMapping("/teams/{teamId}/members/{memberId}", method = [RequestMethod.GET])
    open fun getTeamMember(@PathVariable("teamId") teamId: Int,
                           @PathVariable("memberId") memberId: Int,
                           authentication: Authentication): ResponseEntity<TeamMemberResource> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val teamMember = teamMemberRepository.findOne(memberId)
            return ResponseEntity.ok(TeamMemberResource(teamMember))
        }
        throw UserForbiddenException()
    }
}
