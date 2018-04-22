package com.zcorp.opensportmanagement.rest.resources

import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.model.Opponent
import com.zcorp.opensportmanagement.model.TeamMember
import com.zcorp.opensportmanagement.rest.ChampionshipController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

class MatchResource(val id: Int, val opponent: Opponent, val presentPlayers: Set<TeamMember>, val notPresentPlayers: Set<TeamMember>, championshipId: Int) : ResourceSupport() {
    constructor(m: Match) : this(m.id, m.opponent, m.getPresentPlayers(), m.getAbsentPlayers(), m.championship.id)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ChampionshipController::class.java).getMatches(championshipId, UsernamePasswordAuthenticationToken(null, null))).withSelfRel())
    }
}