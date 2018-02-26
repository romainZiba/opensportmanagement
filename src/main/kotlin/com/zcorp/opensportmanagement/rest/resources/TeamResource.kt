package com.zcorp.opensportmanagement.rest.resources

import com.zcorp.opensportmanagement.rest.*
import com.zcorp.opensportmanagement.model.Team
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

data class TeamResource(val _id: Int, val name: String, val sport: Team.Sport, val genderKind: Team.Gender, val ageGroup: Team.AgeGroup) : ResourceSupport() {

    constructor(t: Team) : this(t.id, t.name, t.sport, t.genderKind, t.ageGroup)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TeamController::class.java).getTeam(_id, UsernamePasswordAuthenticationToken(null, null))).withSelfRel())
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TeamMemberController::class.java).getTeamMembers(_id, UsernamePasswordAuthenticationToken(null, null))).withRel("members"))
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(StadiumController::class.java).getStadiums(_id, UsernamePasswordAuthenticationToken(null, null))).withRel("stadiums"))
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(SeasonController::class.java).getSeasons(_id, UsernamePasswordAuthenticationToken(null, null))).withRel("seasons"))
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(EventController::class.java).getEvents(_id, UsernamePasswordAuthenticationToken(null, null))).withRel("events"))
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(OpponentController::class.java).getOpponents(_id, UsernamePasswordAuthenticationToken(null, null))).withRel("opponents"))
    }
}