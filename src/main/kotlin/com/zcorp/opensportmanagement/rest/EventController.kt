package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.repositories.EventRepository
import com.zcorp.opensportmanagement.rest.resources.EventResource
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@RepositoryRestController
@RequestMapping("/events")
open class EventController @Autowired constructor(private val eventRepository: EventRepository,
                                                  private val accessController: AccessController) {

    @GetMapping("/{eventId}")
    open fun getEvent(@PathVariable("eventId") eventId: Int,
                      authentication: Authentication): ResponseEntity<EventResource> {
        val event = eventRepository.getOne(eventId) ?: throw UserForbiddenException()
        val team = event.team ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, team.id)) {
            return ResponseEntity.ok(EventResource(event))
        }
        throw UserForbiddenException()
    }

    @DeleteMapping("/{eventId}")
    open fun deleteEvent(@PathVariable("eventId") eventId: Int,
                         authentication: Authentication): ResponseEntity<Any> {
        val event = eventRepository.getOne(eventId) ?: throw UserForbiddenException()
        val team = event.team ?: throw UserForbiddenException()
        if (accessController.isTeamAdmin(authentication, team.id)) {
            eventRepository.deleteById(eventId)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }
}
