package com.zcorp.opensportmanagement.controllers

import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.model.EventResource
import com.zcorp.opensportmanagement.model.StadiumResource
import com.zcorp.opensportmanagement.repositories.EventRepository
import com.zcorp.opensportmanagement.repositories.StadiumRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@RepositoryRestController
open class EventController(private val teamRepository: TeamRepository,
                           private val eventRepository: EventRepository,
                           private val accessController: AccessController) {

    @RequestMapping("/teams/{teamId}/events", method = [RequestMethod.GET])
    open fun getEvents(@PathVariable("teamId") teamId: Int, authentication: Authentication): ResponseEntity<List<EventResource>> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val team = teamRepository.findOne(teamId)
            return ResponseEntity.ok(team.events.map { event -> EventResource(event) })
        }
        throw UserForbiddenException()
    }

    @RequestMapping("/teams/{teamId}/events/{eventId}", method = [RequestMethod.GET])
    open fun getEvent(@PathVariable("teamId") teamId: Int,
                        @PathVariable("eventId") eventId: Int,
                        authentication: Authentication): ResponseEntity<EventResource> {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) {
            val event = eventRepository.findOne(eventId)
            return ResponseEntity.ok(EventResource(event))
        }
        throw UserForbiddenException()
    }
}
