package com.zcorp.opensportmanagement.rest.resources

import com.fasterxml.jackson.annotation.JsonIgnore
import com.zcorp.opensportmanagement.model.AbstractEvent
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.rest.TeamController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.time.LocalDateTime

class EventResource(val _id: Int,
                    val name: String,
                    val description: String,
                    val fromDate: LocalDateTime?,
                    val toDate: LocalDateTime?,
                    val place: String?,
                    @JsonIgnore val teamId: Int) : ResourceSupport() {

    var localTeamName: String? = null
    var visitorTeamName: String? = null
    var localTeamImgUrl: String? = null
    var visitorTeamImgUrl: String? = null
    var visitorTeamScore: Int? = null
    var localTeamScore: Int? = null
    var isDone: Boolean? = null


    constructor(event: AbstractEvent) : this(event.id, event.name, event.description, event.fromDateTime,
            event.toDateTime, event.place, event.team!!.id) {
        if (event is Match) {
            this.isDone = event.isDone
            if (event.isTeamLocal) {
                this.localTeamName = event.team.name
                this.localTeamImgUrl = event.team.imgUrl
                this.localTeamScore = event.teamScore
                this.visitorTeamName = event.opponent.imgUrl
                this.visitorTeamImgUrl = event.opponent.imgUrl
                this.visitorTeamScore = event.opponentScore
            } else {
                this.localTeamName = event.opponent.name
                this.localTeamImgUrl = event.opponent.imgUrl
                this.localTeamScore = event.opponentScore
                this.visitorTeamName = event.team.name
                this.visitorTeamImgUrl = event.team.imgUrl
                this.visitorTeamScore = event.teamScore
            }
        }

    }

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TeamController::class.java).getEvents(teamId, UsernamePasswordAuthenticationToken(null, null))).withSelfRel())
    }
}