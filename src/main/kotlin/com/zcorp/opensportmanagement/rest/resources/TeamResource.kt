package com.zcorp.opensportmanagement.rest.resources

import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.rest.TeamController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

data class TeamResource(val _id: Int, val name: String, val sport: Team.Sport, val genderKind: Team.Gender, val ageGroup: Team.AgeGroup) : ResourceSupport() {

    constructor(t: Team) : this(t.id, t.name, t.sport, t.genderKind, t.ageGroup)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TeamController::class.java).getTeam(_id, UsernamePasswordAuthenticationToken(null, null))).withSelfRel())
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TeamController::class.java).getTeamMembers(_id, UsernamePasswordAuthenticationToken(null, null))).withRel("members"))
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TeamController::class.java).getStadiums(_id, UsernamePasswordAuthenticationToken(null, null))).withRel("stadiums"))
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TeamController::class.java).getSeasons(_id, UsernamePasswordAuthenticationToken(null, null))).withRel("seasons"))
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TeamController::class.java).getEvents(_id, UsernamePasswordAuthenticationToken(null, null))).withRel("events"))
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TeamController::class.java).getOpponents(_id, UsernamePasswordAuthenticationToken(null, null))).withRel("opponents"))
    }
}