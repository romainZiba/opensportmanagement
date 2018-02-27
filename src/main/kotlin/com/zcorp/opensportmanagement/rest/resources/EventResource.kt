package com.zcorp.opensportmanagement.rest.resources

import com.fasterxml.jackson.annotation.JsonIgnore
import com.zcorp.opensportmanagement.model.AbstractEvent
import com.zcorp.opensportmanagement.rest.EventController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.time.LocalDateTime

class EventResource(val _id: Int, val name: String, val description: String, val fromDate: LocalDateTime?,
                    val toDate: LocalDateTime?, val place: String?, @JsonIgnore val teamId: Int) : ResourceSupport() {
    constructor(e: AbstractEvent) : this(e.id, e.name, e.description, e.fromDateTime, e.toDateTime, e.place, e.team!!.id)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(EventController::class.java).getEvents(teamId, UsernamePasswordAuthenticationToken(null, null))).withSelfRel())
    }
}