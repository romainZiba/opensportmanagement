package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.EventCreationDto
import com.zcorp.opensportmanagement.model.AbstractEvent
import com.zcorp.opensportmanagement.model.Event
import com.zcorp.opensportmanagement.model.Stadium
import com.zcorp.opensportmanagement.repositories.EventRepository
import com.zcorp.opensportmanagement.repositories.StadiumRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
open class EventService @Autowired constructor(private val eventRepository: EventRepository,
                                               private val stadiumRepository: StadiumRepository,
                                               private val teamRepository: TeamRepository) {

    @Transactional
    open fun getEvent(eventId: Int): AbstractEvent? {
        return eventRepository.getEventById(eventId)
    }

    @Transactional
    open fun deleteEvent(eventId: Int) {
        return eventRepository.deleteById(eventId)
    }

    @Transactional
    open fun createEvent(teamId: Int, dto: EventCreationDto) {
        val team = teamRepository.getOne(teamId)
        var stadium: Stadium? = null
        if (dto.stadiumId != null) {
            stadium = stadiumRepository.getOne(dto.stadiumId) ?: throw EntityNotFoundException()
        }

        if (dto.isRecurrent) {
            val fromDate = dto.recurrenceFromDate ?: throw Exception()
            val toDate = dto.recurrenceToDate ?: throw Exception()
            val fromTime = dto.recurrenceFromTime ?: throw Exception()
            val toTime = dto.recurrenceToTime ?: throw Exception()
            val daysOfWeek = dto.recurrenceDays ?: throw Exception()

            var currentDate = fromDate
            val events = mutableListOf<Event>()
            while (!currentDate.isAfter(toDate)) {
                if (daysOfWeek.contains(currentDate.dayOfWeek)) {
                    if (stadium == null) {
                        val event = Event(dto.name,
                                LocalDateTime.of(currentDate, fromTime),
                                LocalDateTime.of(currentDate, toTime),
                                dto.place!!,
                                team)
                        events.add(event)
                    } else {
                        val event = Event(dto.name,
                                LocalDateTime.of(currentDate, fromTime),
                                LocalDateTime.of(currentDate, toTime),
                                stadium,
                                team)
                        events.add(event)
                    }
                }
                currentDate = currentDate.plusDays(1)
            }
            eventRepository.saveAll(events)
        } else {
            val fromDateTime = dto.fromDate ?: throw Exception()
            val toDateTime = dto.toDate ?: throw Exception()
            if (stadium == null) {
                val place = dto.place ?: throw Exception()
                eventRepository.save(Event(dto.name, fromDateTime, toDateTime, place, team))
            } else {
                eventRepository.save(Event(dto.name, fromDateTime, toDateTime, stadium, team))

            }
        }
    }
}