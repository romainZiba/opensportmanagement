package com.zcorp.opensportmanagement.controllers

import com.zcorp.opensportmanagement.BadInputException
import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.model.EventResource
import com.zcorp.opensportmanagement.model.OtherEvent
import com.zcorp.opensportmanagement.model.OtherEventDto
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.repositories.EventRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.net.URI

@RepositoryRestController
open class EventController(private val teamRepository: TeamRepository,
                           private val eventRepository: EventRepository,
                           private val accessController: AccessController) {

    @RequestMapping("/teams/{teamId}/events", method = [RequestMethod.POST])
    open fun createEvent(@PathVariable("teamId") teamId: Int,
                         @RequestBody eventDto: OtherEventDto,
                         authentication: Authentication): ResponseEntity<Any> {
        if (accessController.isTeamAdmin(authentication, teamId)) {
            val team = teamRepository.findOne(teamId)
            val event = createEventFromDto(eventDto, team)
            team.events.add(event)
            teamRepository.save(team)
            return ResponseEntity.created(URI("")).build()
        }
        throw UserForbiddenException()
    }

    @RequestMapping("/teams/{teamId}/events", method = [RequestMethod.GET])
    open fun getEvents(@PathVariable("teamId") teamId: Int, authentication: Authentication): ResponseEntity<List<EventResource>> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val team = teamRepository.findOne(teamId)
            return ResponseEntity.ok(team.events.map { event -> EventResource(event) })
        }
        throw UserForbiddenException()
    }

    @RequestMapping("/events/{eventId}", method = [RequestMethod.GET])
    open fun getEvent(@PathVariable("eventId") eventId: Int,
                      authentication: Authentication): ResponseEntity<EventResource> {
        val event = eventRepository.findOne(eventId) ?: throw UserForbiddenException()
        val team = event.team ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, team.id)) {
            return ResponseEntity.ok(EventResource(event))
        }
        throw UserForbiddenException()
    }

    @RequestMapping("/events/{eventId}", method = [RequestMethod.DELETE])
    open fun deleteEvent(@PathVariable("eventId") eventId: Int,
                         authentication: Authentication): ResponseEntity<Any> {
        val event = eventRepository.findOne(eventId) ?: throw UserForbiddenException()
        val team = event.team ?: throw UserForbiddenException()
        if (accessController.isTeamAdmin(authentication, team.id)) {
            eventRepository.delete(eventId)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }

    private fun createEventFromDto(eventDto: OtherEventDto, team: Team): OtherEvent {
        val place = eventDto.place
        val stadium = eventDto.stadium
        if (place == null && stadium == null) {
            throw BadInputException("Either stadium or place must be provided")
        }
        val recurrenceDays = eventDto.reccurenceDays
        val fromDateTime = eventDto.fromDateTime
        val toDateTime = eventDto.toDateTime
        val recurrenceFromTime = eventDto.recurrenceFromTime
        val recurrenceToTime = eventDto.recurrenceToTime
        if ((recurrenceDays == null || recurrenceFromTime == null || recurrenceToTime == null)
                && (fromDateTime == null || toDateTime == null)) {
            throw BadInputException("Either recurrence or fixed event information must be provided")
        }

        if (eventDto.reccurenceDays == null) {
            return if (stadium == null) {
                OtherEvent(eventDto.name, eventDto.description, fromDateTime!!, toDateTime!!,
                        place!!, team)
            } else {
                OtherEvent(eventDto.name, eventDto.description, fromDateTime!!, toDateTime!!,
                        stadium, team)
            }
        } else {
            return if (stadium == null) {
                OtherEvent(eventDto.name, eventDto.description, eventDto.reccurenceDays, eventDto.recurrenceFromDate!!,
                        eventDto.recurrenceToDate!!, recurrenceFromTime!!, recurrenceToTime!!, place!!, team)
            } else {
                OtherEvent(eventDto.name, eventDto.description, eventDto.reccurenceDays, eventDto.recurrenceFromDate!!,
                        eventDto.recurrenceToDate!!, recurrenceFromTime!!, recurrenceToTime!!, stadium, team)
            }
        }
    }
}
