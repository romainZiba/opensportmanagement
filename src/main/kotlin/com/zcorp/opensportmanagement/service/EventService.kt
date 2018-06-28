package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.config.EventsProperties
import com.zcorp.opensportmanagement.dto.EventCreationDto
import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.dto.EventModificationDto
import com.zcorp.opensportmanagement.model.AbstractEvent
import com.zcorp.opensportmanagement.model.Event
import com.zcorp.opensportmanagement.model.MemberResponse
import com.zcorp.opensportmanagement.repository.EventRepository
import com.zcorp.opensportmanagement.repository.MemberResponseRepository
import com.zcorp.opensportmanagement.repository.PlaceRepository
import com.zcorp.opensportmanagement.repository.TeamMemberRepository
import com.zcorp.opensportmanagement.repository.TeamRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional

@Service
open class EventService @Autowired constructor(
    private val eventRepository: EventRepository,
    private val memberResponseRepository: MemberResponseRepository,
    private val teamMemberRepository: TeamMemberRepository,
    private val placeRepository: PlaceRepository,
    private val teamRepository: TeamRepository,
    private val emailService: EmailService,
    private val properties: EventsProperties
) {
    @Transactional
    open fun getEvent(eventId: Int): EventDto {
        return eventRepository.getEventById(eventId)?.toDto() ?: throw NotFoundException("Event $eventId not found")
    }

    @Transactional
    open fun notifyEvents(comparedDate: LocalDateTime): Int {
        val events = eventRepository.getEventsByNotifiedFalseAndFromDateTimeBefore(comparedDate)
        for (event in events) {
            val noResponseMembers = eventRepository.getMembersThatHaveNotResponded(event.id)
            val teamMembersToNotify = noResponseMembers.map { it.account.email }.toList()
            emailService.sendMessage(teamMembersToNotify, "L'évènement ${event.name} approche!", "Inscris toi donc")
            event.notified = true
        }
        return events.size
    }

    @Transactional
    open fun deleteEvent(eventId: Int) {
        eventRepository.deleteById(eventId)
    }

    @Transactional
    @Throws(BadParameterException::class)
    open fun createEvent(teamId: Int, dto: EventCreationDto) {
        val eventType = dto.type
        val eventName = when (eventType) {
        // TODO: i18n
            AbstractEvent.EventType.TRAINING -> "Entrainement"
            else -> (dto.name ?: "Evènement")
        }
        if (eventName.isEmpty()) {
            throw BadParameterException("Name of the event must not be empty")
        }
        val team = teamRepository.findById(teamId)
                .orElseThrow { NotFoundException("Team $teamId does not exist") }
        val eventBuilder = Event.Builder().name(eventName)
                .team(team)
        val placeId = dto.placeId
        val place = placeRepository.findById(placeId)
                .orElseThrow { NotFoundException("Place $placeId does not exist") }
        eventBuilder.place(place)
        if (dto.isRecurrent) {
            val daysOfWeek = dto.recurrenceDays
            if (daysOfWeek.isEmpty()) {
                throw BadParameterException("At least one day has to be selected")
            }
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
    }

    @Transactional
    open fun participate(username: String, eventId: Int, present: Boolean, now: LocalDateTime): EventDto {
        val event = eventRepository.findById(eventId).orElseThrow { NotFoundException("Event $eventId does not exist") }
        val teamMember = teamMemberRepository.findByUsername(username, event.team.id!!)
                ?: throw NotFoundException("Team member $username does not exist")
        when {
            event.fromDateTime.isBefore(now) ->
                throw SubscriptionNotPermittedException("Event $eventId has already occurred")
            event.fromDateTime.isAfter(now.plusDays(properties.daysBefore)) ->
                throw SubscriptionNotPermittedException("Event $eventId is not open for subscriptions yet")
        }
        val eventFull = event.isFull()
        val status = when {
            present && eventFull -> MemberResponse.Status.WAITING
            present -> MemberResponse.Status.PRESENT
            !present -> MemberResponse.Status.ABSENT
            else -> MemberResponse.Status.ABSENT
        }
        if (!present && eventFull && event.isMemberPresent(username)) {
            val firstWaitingMember = event.membersResponse
                    .filter { it.status == MemberResponse.Status.WAITING }
                    .sortedBy { it.subscriptionDate }
                    .map { it.teamMember }
                    .firstOrNull()
            if (firstWaitingMember != null) {
                memberResponseRepository.save(MemberResponse(event, firstWaitingMember, MemberResponse.Status.PRESENT))
                // TODO: better mail please...
                emailService.sendMessage(
                        to = listOf(firstWaitingMember.account.email),
                        subject = "Convoqué !",
                        text = "Tu participes à ${event.name}")
            }
        }
        memberResponseRepository.save(MemberResponse(event, teamMember, status))
        return event.toDto()
    }

    @Transactional
    open fun updateEvent(eventId: Int, dto: EventModificationDto, comparedDate: LocalDateTime): EventDto {
        val event = eventRepository.findById(eventId).orElseThrow { NotFoundException("Event $eventId does not exist") }
        val place = placeRepository.findById(dto.placeId).orElseThrow { NotFoundException("Place ${dto.placeId} does not exist") }
        val dtoFromDateTime = LocalDateTime.of(dto.fromDate, dto.fromTime)
        val dtoToDateTime = LocalDateTime.of(dto.toDate, dto.toTime)
        if (dtoFromDateTime.isBefore(comparedDate)) throw NotPossibleException("From date can not be in the past")
        if (dtoFromDateTime.isAfter(dtoToDateTime)) throw NotPossibleException("To date must be greater than from date")
        if (event.fromDateTime.isBefore(comparedDate)) throw NotPossibleException("Event has already occurred")
        event.fromDateTime = dtoFromDateTime
        event.toDateTime = dtoToDateTime
        event.place = place
        return eventRepository.save(event).toDto()
    }
}