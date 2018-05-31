package com.zcorp.opensportmanagement.mail

import com.zcorp.opensportmanagement.config.NotificationsProperties
import com.zcorp.opensportmanagement.service.EventService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class EventInvitationsTask @Autowired constructor(
    private val eventService: EventService,
    private val properties: NotificationsProperties
) {
    @Scheduled(fixedRate = 30000, initialDelay = 10000)
    fun sendEmailInvitations() {
        if (properties.enabled) {
            eventService.notifyEvents(LocalDateTime.now().plusDays(properties.daysBefore))
        }
    }
}