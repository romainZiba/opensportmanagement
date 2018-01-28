package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.controllers.EventController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "event")
class OtherEvent : Event {

    constructor(name: String, description: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime,
                stadium: Stadium, team: Team) :
            super(name, description, fromDateTime, toDateTime, stadium, team)

    constructor(name: String, description: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime,
                place: String, team: Team) :
            super(name, description, fromDateTime, toDateTime, place, team)

    constructor(name: String, description: String, reccurenceDays: MutableSet<DayOfWeek>, recurrenceFromDate: LocalDate,
                recurrenceToDate: LocalDate, recurrenceFromTime: LocalTime, recurrenceToTime: LocalTime, stadium: Stadium,
                team: Team) :
            super(name, description, reccurenceDays, recurrenceFromDate, recurrenceToDate, recurrenceFromTime,
                    recurrenceToTime, stadium, team)

    constructor(name: String, description: String, reccurenceDays: MutableSet<DayOfWeek>, recurrenceFromDate: LocalDate,
                recurrenceToDate: LocalDate, recurrenceFromTime: LocalTime, recurrenceToTime: LocalTime, place: String,
                team: Team) :
            super(name, description, reccurenceDays, recurrenceFromDate, recurrenceToDate, recurrenceFromTime,
                    recurrenceToTime, place, team)

    override fun toString(): String {
        return "OtherEvent() ${super.toString()}"
    }
}

class OtherEventDto(val name: String, val description: String, val fromDateTime: LocalDateTime?, val toDateTime: LocalDateTime?,
                    val stadium: Stadium?, val place: String?, val reccurenceDays: MutableSet<DayOfWeek>?,
                    val recurrenceFromDate: LocalDate?, val recurrenceToDate: LocalDate?, val recurrenceFromTime: LocalTime?,
                    val recurrenceToTime: LocalTime?)

// Resource with self links
class EventResource(val name: String, val description: String, val teamId: Int) : ResourceSupport() {
    constructor(e: Event) : this(e.name, e.description, e.team!!.id)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(EventController::class.java).getEvents(teamId, UsernamePasswordAuthenticationToken(null, null))).withSelfRel())
    }
}