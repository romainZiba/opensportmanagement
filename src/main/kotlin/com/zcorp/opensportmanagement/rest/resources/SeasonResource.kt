package com.zcorp.opensportmanagement.rest.resources

import com.zcorp.opensportmanagement.model.Season
import com.zcorp.opensportmanagement.rest.ChampionshipController
import com.zcorp.opensportmanagement.rest.SeasonController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.time.LocalDate

data class SeasonResource(val _id: Int, val name: String, val fromDate: LocalDate, val toDate: LocalDate, val status: Season.Status,
                          val teamId: Int) : ResourceSupport() {
    constructor(s: Season) : this(s.id, s.name, s.fromDate, s.toDate, s.status, s.team.id)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(SeasonController::class.java).getSeasons(teamId, UsernamePasswordAuthenticationToken(null, null))).withSelfRel())
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ChampionshipController::class.java).getChampionships(_id, UsernamePasswordAuthenticationToken(null, null))).withRel("championships"))
    }
}