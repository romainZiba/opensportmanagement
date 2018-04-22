package com.zcorp.opensportmanagement.rest.resources

import com.zcorp.opensportmanagement.model.Stadium
import com.zcorp.opensportmanagement.rest.TeamController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

data class StadiumResource(val name: String, val address: String, val city: String, val teamId: Int) : ResourceSupport() {
    constructor(s: Stadium) : this(s.name, s.address, s.city, s.team.id)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TeamController::class.java).getStadiums(teamId, UsernamePasswordAuthenticationToken(null, null))).withSelfRel())
    }
}