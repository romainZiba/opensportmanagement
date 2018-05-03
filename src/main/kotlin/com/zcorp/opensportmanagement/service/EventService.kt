package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.model.AbstractEvent
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.repositories.EventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class EventService @Autowired constructor(private val eventRepository: EventRepository) {

    @Transactional
    open fun getEvent(eventId: Int): AbstractEvent? {
        return eventRepository.getEventById(eventId)
    }

    @Transactional
    open fun deleteEvent(eventId: Int) {
        return eventRepository.deleteById(eventId)
    }
}