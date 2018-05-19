package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.EventCreationDto
import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.model.Event
import com.zcorp.opensportmanagement.repositories.EventRepository
import com.zcorp.opensportmanagement.repositories.PlaceRepository
import com.zcorp.opensportmanagement.repositories.TeamMemberRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException
import javax.persistence.NoResultException
import javax.transaction.Transactional

@Service
open class EventService @Autowired constructor(private val eventRepository: EventRepository,
                                               private val teamMemberRepository: TeamMemberRepository,
                                               private val placeRepository: PlaceRepository,
                                               private val teamRepository: TeamRepository) {

    @Transactional
    open fun getEvent(eventId: Int): EventDto {
        try {
            return eventRepository.getEventById(eventId).toDto()
        } catch (e: NoResultException) {
            throw NotFoundException("Event $eventId does not exist")
        }
    }

    @Transactional
    open fun deleteEvent(eventId: Int) {
        eventRepository.deleteById(eventId)
    }

    @Transactional
    open fun createEvent(teamId: Int, dto: EventCreationDto) {
        try {
            val team = teamRepository.getOne(teamId)
            val eventBuilder = Event.Builder().name(dto.name).team(team)
            val place = placeRepository.getOne(dto.placeId)
            eventBuilder.place(place)
            if (dto.isRecurrent) {
                val daysOfWeek = dto.recurrenceDays
                var currentDate = dto.fromDate
                val events = mutableListOf<Event>()
                while (!currentDate.isAfter(dto.toDate)) {
                    if (daysOfWeek.contains(currentDate.dayOfWeek)) {
                        eventBuilder.fromDate(LocalDateTime.of(currentDate, dto.fromTime))
                                .toDate(LocalDateTime.of(currentDate, dto.toTime))
                        events.add(eventBuilder.build())

                    }
                    currentDate = currentDate.plusDays(1)
                }
                eventRepository.saveAll(events)
            } else {
                val fromDateTime = LocalDateTime.of(dto.fromDate, dto.fromTime)
                val toDateTime = LocalDateTime.of(dto.toDate, dto.toTime)
                eventBuilder.fromDate(fromDateTime).toDate(toDateTime)
                eventRepository.save(eventBuilder.build())
            }
        } catch (e: Exception) {
            throw NotFoundException(e.message ?: "")
        }
    }

    @Transactional
    open fun participate(username: String, eventId: Int, present: Boolean): EventDto {
        try {
            val event = eventRepository.getOne(eventId)
            val teamMember = teamMemberRepository.findByUsername(username, event.team.id) ?: throw NotFoundException("Team member $username does not exist")
            if (event.fromDateTime.isBefore(LocalDateTime.now())) {
                throw PastEventException(eventId)
            }
            event.participate(teamMember, present)
            return eventRepository.save(event).toDto()
        } catch (e: EntityNotFoundException) {
            throw NotFoundException("Event $eventId does not exist")
        }
    }
}