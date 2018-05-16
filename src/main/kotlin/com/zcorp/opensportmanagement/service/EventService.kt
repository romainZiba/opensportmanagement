package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.EventCreationDto
import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.model.Event
import com.zcorp.opensportmanagement.repositories.EventRepository
import com.zcorp.opensportmanagement.repositories.StadiumRepository
import com.zcorp.opensportmanagement.repositories.TeamMemberRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.rest.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException
import javax.persistence.NoResultException
import javax.transaction.Transactional

@Service
open class EventService @Autowired constructor(private val eventRepository: EventRepository,
                                               private val teamMemberRepository: TeamMemberRepository,
                                               private val stadiumRepository: StadiumRepository,
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
            if (dto.stadiumId != null) {
                val stadium = stadiumRepository.getOne(dto.stadiumId) ?: throw EntityNotFoundException()
                eventBuilder.stadium(stadium)
            } else {
                val place = dto.place ?: throw MissingParameterException("place")
                eventBuilder.place(place)
            }
            if (dto.isRecurrent) {
                val fromDate = dto.recurrenceFromDate ?: throw MissingParameterException("recurrenceFromDate")
                val toDate = dto.recurrenceToDate ?: throw MissingParameterException("recurrenceToDate")
                val fromTime = dto.recurrenceFromTime ?: throw MissingParameterException("recurrenceFromTime")
                val toTime = dto.recurrenceToTime ?: throw MissingParameterException("recurrenceToTime")
                val daysOfWeek = dto.recurrenceDays ?: throw MissingParameterException("recurrenceDays")

                var currentDate = fromDate
                val events = mutableListOf<Event>()
                while (!currentDate.isAfter(toDate)) {
                    if (daysOfWeek.contains(currentDate.dayOfWeek)) {
                        eventBuilder.fromDate(LocalDateTime.of(currentDate, fromTime))
                                .toDate(LocalDateTime.of(currentDate, toTime))
                        events.add(eventBuilder.build())

                    }
                    currentDate = currentDate.plusDays(1)
                }
                eventRepository.saveAll(events)
            } else {
                val fromDateTime = dto.fromDate ?: throw MissingParameterException("fromDate")
                val toDateTime = dto.toDate ?: throw MissingParameterException("toDate")
                eventBuilder.fromDate(fromDateTime).toDate(toDateTime)
                eventRepository.save(eventBuilder.build())
            }
        } catch (e: EntityNotFoundException) {
            throw NotFoundException("Team $teamId does not exist")
        }
    }

    @Transactional
    open fun participate(username: String, eventId: Int, present: Boolean): EventDto {
        try {
            val event = eventRepository.getOne(eventId)
            val teamMember = teamMemberRepository.findByUsername(username, event.team.id) ?: throw NotFoundException("Team member $username does not exist")
            event.participate(teamMember, present)
            return eventRepository.save(event).toDto()
        } catch (e: EntityNotFoundException) {
            throw NotFoundException("Event $eventId does not exist")
        }
    }
}