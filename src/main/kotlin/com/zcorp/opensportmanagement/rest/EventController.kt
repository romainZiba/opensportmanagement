package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.config.OsmProperties
import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.dto.EventModificationDto
import com.zcorp.opensportmanagement.dto.MessageCreationDto
import com.zcorp.opensportmanagement.dto.MessageDto
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.EmailService
import com.zcorp.opensportmanagement.service.EventService
import com.zcorp.opensportmanagement.service.MessagingService
import com.zcorp.opensportmanagement.service.NotPossibleException
import org.slf4j.LoggerFactory
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

@RepositoryRestController
@RequestMapping("/events")
open class EventController @Autowired constructor(
    private val eventService: EventService,
    private val messagingService: MessagingService,
    private val accessController: AccessController,
    private val mailService: EmailService,
    private val properties: OsmProperties
) {
    companion object {
        val LOG = LoggerFactory.getLogger(EventController::class.java)
    }

    @GetMapping("/{eventId}")
    open fun getEvent(
        @PathVariable("eventId") eventId: Int,
        authentication: Authentication
    ): ResponseEntity<EventDto> {
        val eventDto = eventService.getEvent(eventId)
        if (accessController.isAccountAllowedToAccessTeam(authentication, eventDto.teamId)) {
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
        if (accessController.isTeamAdmin(authentication, eventDto.teamId)) {
            eventService.deleteEvent(eventId)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }

    @PutMapping("/{eventId}")
    open fun modifyEvent(
        @PathVariable("eventId") eventId: Int,
        @RequestBody eventModifDto: EventModificationDto,
        authentication: Authentication
    ): ResponseEntity<EventDto> {
        val eventDto = eventService.getEvent(eventId)
        if (accessController.isTeamAdmin(authentication, eventDto.teamId)) {
            val dto = eventService.updateEvent(eventId, eventModifDto, LocalDateTime.now())
            return ResponseEntity.ok(dto)
        }
        throw UserForbiddenException()
    }

    @PutMapping("/{eventId}/{present}")
    open fun participate(
        @PathVariable("eventId") eventId: Int,
        @PathVariable("present") present: Boolean,
        authentication: Authentication
    ): ResponseEntity<EventDto> {
        val eventDto = eventService.getEvent(eventId)
        if (accessController.isAccountAllowedToAccessTeam(authentication, eventDto.teamId)) {
            val dto = eventService.participate(authentication.name, eventId, present, LocalDateTime.now())
            return ResponseEntity.ok(dto)
        }
        throw UserForbiddenException()
    }

    @PutMapping("/{eventId}/cancelled")
    open fun cancel(
        @PathVariable("eventId") eventId: Int,
        authentication: Authentication
    ): ResponseEntity<EventDto> {
        val eventDto = eventService.getEvent(eventId)
        if (accessController.isTeamAdmin(authentication, eventDto.teamId)) {
            val updatedDto = eventService.cancelEvent(eventId, LocalDateTime.now())
            return ResponseEntity.ok(updatedDto)
        }
        throw UserForbiddenException()
    }

    @GetMapping("/{eventId}/messages")
    fun getMessages(
        @PathVariable("eventId") eventId: Int,
        authentication: Authentication
    ): ResponseEntity<List<MessageDto>> {
        val eventDto = eventService.getEvent(eventId)
        if (accessController.isAccountAllowedToAccessTeam(authentication, eventDto.teamId)) {
            return ResponseEntity.ok(messagingService.getMessagesFromEvent(eventId))
        }
        throw UserForbiddenException()
    }

    @PostMapping("/{eventId}/messages")
    fun createMessageInEvent(
        @PathVariable("eventId") eventId: Int,
        @RequestBody messageDto: MessageCreationDto,
        authentication: Authentication
    ): ResponseEntity<MessageDto> {
        val eventDto = eventService.getEvent(eventId)
        if (accessController.isAccountAllowedToAccessTeam(authentication, eventDto.teamId)) {
            return ResponseEntity.ok(messagingService.createMessageInEvent(messageDto, authentication.name, eventDto))
        }
        throw UserForbiddenException()
    }

    @PostMapping("/{eventId}/notifications")
    fun notifyUsersThatHaveNotResponded(
        @PathVariable("eventId") eventId: Int,
        authentication: Authentication
    ): ResponseEntity<Any> {
        val eventDto = eventService.getEvent(eventId)
        if (accessController.isTeamAdmin(authentication, eventDto.teamId)) {
            if (!eventDto.openForRegistration) throw NotPossibleException("Event $eventId is not open for registration")
            val toNotify = eventService.getMembersMailNotResponded(eventId)
            if (toNotify.isNotEmpty()) {
                mailService.sendMessage(
                        to = toNotify,
                        subject = "Répond!",
                        text = "Merci d'indiquer ta disponibilité pour l'évènement ${eventDto.name} en suivant ce lien " +
                                "${properties.allowedOrigins[0]}/events/$eventId")
            }
            return ResponseEntity.ok().build()
        }
        throw UserForbiddenException()
    }

    @PutMapping("/{eventId}/registrations")
    fun changeRegistration(
        @PathVariable("eventId") eventId: Int,
        authentication: Authentication
    ): ResponseEntity<Any> {
        val event = eventService.getEvent(eventId)
        if (accessController.isTeamAdmin(authentication, event.teamId)) {
            eventService.openEvent(event._id)
            return ResponseEntity.ok().build()
        }
        throw UserForbiddenException()
    }
}
