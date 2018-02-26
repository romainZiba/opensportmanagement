package com.zcorp.opensportmanagement.rest.resources

import com.zcorp.opensportmanagement.model.Championship
import com.zcorp.opensportmanagement.rest.ChampionshipController
import com.zcorp.opensportmanagement.rest.MatchController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

data class ChampionshipResource(val _id: Int, val name: String, val seasonId: Int) : ResourceSupport() {
    constructor(c: Championship) : this(c.id, c.name, c.season.id)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ChampionshipController::class.java).getChampionships(seasonId, UsernamePasswordAuthenticationToken(null, null))).withSelfRel().withRel("championships"))
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(MatchController::class.java).getMatches(_id, UsernamePasswordAuthenticationToken(null, null))).withRel("matches"))
    }
}