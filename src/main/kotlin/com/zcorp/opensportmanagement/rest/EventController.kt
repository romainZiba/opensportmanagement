package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.dto.MessageCreationDto
import com.zcorp.opensportmanagement.dto.MessageDto
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.EventService
import com.zcorp.opensportmanagement.service.MessagingService
import com.zcorp.opensportmanagement.service.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

@RepositoryRestController
@RequestMapping("/events")
open class EventController @Autowired constructor(
    private val eventService: EventService,
    private val messagingService: MessagingService,
    private val accessController: AccessController
) {

    @GetMapping("/{eventId}")
    open fun getEvent(
        @PathVariable("eventId") eventId: Int,
        authentication: Authentication
    ): ResponseEntity<EventDto> {
        val eventDto = eventService.getEvent(eventId)
        if (accessController.isAccountAllowedToAccessTeam(authentication, eventDto.teamId!!)) {
            return ResponseEntity.ok(eventDto)
        }
        throw UserForbiddenException()
    }

    @DeleteMapping("/{eventId}")
    open fun deleteEvent(
        @PathVariable("eventId") eventId: Int,
        authentication: Authentication
    ): ResponseEntity<Any> {
        val eventDto = eventService.getEvent(eventId)
        if (accessController.isTeamAdmin(authentication, eventDto.teamId!!)) {
            eventService.deleteEvent(eventId)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }

    @PutMapping("/{eventId}/{present}")
    open fun participate(
        @NotNull @PathVariable("eventId") eventId: Int,
        @NotNull @PathVariable("present") present: Boolean,
        authentication: Authentication
    ): ResponseEntity<EventDto> {
        var eventDto = eventService.getEvent(eventId)
        if (accessController.isAccountAllowedToAccessTeam(authentication, eventDto.teamId!!)) {
            eventDto = eventService.participate(authentication.name, eventId, present, LocalDateTime.now())
            return ResponseEntity.ok(eventDto)
        }
        throw NotFoundException("Match not found")
    }

    @GetMapping("/{eventId}/messages")
    fun getMessages(
        @NotNull @PathVariable("eventId") eventId: Int,
        authentication: Authentication
    ): ResponseEntity<List<MessageDto>> {
        val eventDto = eventService.getEvent(eventId)
        if (accessController.isAccountAllowedToAccessTeam(authentication, eventDto.teamId!!)) {
            return ResponseEntity.ok(messagingService.getMessagesFromEvent(eventId))
        }
        throw UserForbiddenException()
    }

    @PostMapping("/{eventId}/messages")
    fun createMessageInEvent(
        @NotNull @PathVariable("eventId") eventId: Int,
        @RequestBody messageDto: MessageCreationDto,
        authentication: Authentication
    ): ResponseEntity<MessageDto> {
        val eventDto = eventService.getEvent(eventId)
        if (accessController.isAccountAllowedToAccessTeam(authentication, eventDto.teamId!!)) {
            return ResponseEntity.ok(messagingService.createMessageInEvent(messageDto, authentication.name, eventDto))
        }
        throw UserForbiddenException()
    }
}
