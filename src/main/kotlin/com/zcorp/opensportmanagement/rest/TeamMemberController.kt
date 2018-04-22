package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.repositories.TeamMemberRepository
import com.zcorp.opensportmanagement.rest.resources.TeamMemberResource
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@RepositoryRestController
@RequestMapping("/members")
open class TeamMemberController @Autowired constructor(private val teamMemberRepository: TeamMemberRepository,
                                                       private val accessController: AccessController) {

    @GetMapping("/{memberId}")
    open fun getTeamMember(@PathVariable("memberId") memberId: Int,
                           authentication: Authentication): ResponseEntity<TeamMemberResource> {
        val teamMember = teamMemberRepository.findOne(memberId) ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, teamMember.team.id)) {
            return ResponseEntity.ok(TeamMemberResource(teamMember))
        }
        throw UserForbiddenException()
    }
}
