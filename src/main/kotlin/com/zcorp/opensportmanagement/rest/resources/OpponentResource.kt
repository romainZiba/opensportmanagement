package com.zcorp.opensportmanagement.rest.resources

import com.zcorp.opensportmanagement.model.Opponent
import com.zcorp.opensportmanagement.rest.TeamController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

data class OpponentResource(val name: String, val phoneNumber: String, val email: String, val teamId: Int) : ResourceSupport() {

    constructor(o: Opponent) : this(o.name, o.phoneNumber, o.email, o.team.id)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TeamController::class.java).getOpponents(teamId, UsernamePasswordAuthenticationToken(null, null))).withSelfRel())
    }
}