package com.zcorp.opensportmanagement.rest.resources

import com.zcorp.opensportmanagement.model.TeamMember
import com.zcorp.opensportmanagement.rest.TeamController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

data class TeamMemberResource(val username: String, val roles: Set<TeamMember.Role>, val teamId: Int) : ResourceSupport() {

    constructor(t: TeamMember) : this(t.user.username, t.roles, t.team.id)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TeamController::class.java).getTeamMembers(teamId, UsernamePasswordAuthenticationToken(null, null))).withSelfRel())
    }
}