package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.EventService
import com.zcorp.opensportmanagement.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotNull

@RepositoryRestController
@RequestMapping("/events")
open class EventController @Autowired constructor(private val eventService: EventService,
                                                  private val userService: UserService,
                                                  private val accessController: AccessController) {

    @GetMapping("/{eventId}")
    open fun getEvent(@PathVariable("eventId") eventId: Int,
                      authentication: Authentication): ResponseEntity<EventDto> {
        val eventDto = eventService.getEvent(eventId)
        if (accessController.isUserAllowedToAccessTeam(authentication, eventDto.teamId!!)) {
            return ResponseEntity.ok(eventDto)
        }
        throw UserForbiddenException()
    }

    @DeleteMapping("/{eventId}")
    open fun deleteEvent(@PathVariable("eventId") eventId: Int,
                         authentication: Authentication): ResponseEntity<Any> {
        val eventDto = eventService.getEvent(eventId)
        if (accessController.isTeamAdmin(authentication, eventDto.teamId!!)) {
            eventService.deleteEvent(eventId)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }

    @PutMapping("/{eventId}/{present}")
    open fun participate(@NotNull @PathVariable("eventId") eventId: Int,
                         @NotNull @PathVariable("present") present: Boolean,
                         authentication: Authentication): ResponseEntity<EventDto> {
        var eventDto = eventService.getEvent(eventId)
        if (accessController.isUserAllowedToAccessTeam(authentication, eventDto.teamId!!)) {
            eventDto = userService.participate(authentication.name, eventId, present)
            return ResponseEntity.ok(eventDto)
        }
        throw NotFoundException("Match not found")
    }
}
