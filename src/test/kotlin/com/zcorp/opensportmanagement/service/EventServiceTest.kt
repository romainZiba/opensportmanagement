package com.zcorp.opensportmanagement.service

import assertk.assert
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import assertk.assertions.message
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.config.EventsProperties
import com.zcorp.opensportmanagement.dto.EventCreationDto
import com.zcorp.opensportmanagement.dto.EventModificationDto
import com.zcorp.opensportmanagement.model.AbstractEvent
import com.zcorp.opensportmanagement.model.Account
import com.zcorp.opensportmanagement.model.Event
import com.zcorp.opensportmanagement.model.Place
import com.zcorp.opensportmanagement.model.Place.PlaceType
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.model.TeamMember
import com.zcorp.opensportmanagement.repository.EventRepository
import com.zcorp.opensportmanagement.repository.PlaceRepository
import com.zcorp.opensportmanagement.repository.TeamMemberRepository
import com.zcorp.opensportmanagement.repository.TeamRepository
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Optional

class EventServiceTest {
    private val username = "foo"
    private val teamId = 5
    private val placeId = 1
    private val mockTeam = Team("SuperNam", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "", teamId)
    private val mockPlace = Place("The place", "", "Toulouse", PlaceType.STADIUM, mockTeam, placeId)
    private val mockEvent = Event.Builder().name("TheOne")
            .fromDate(LocalDateTime.of(2018, 1, 31, 10, 0))
            .toDate(LocalDateTime.of(2018, 1, 31, 12, 0))
            .place(mockPlace)
            .team(mockTeam)
            .build()
    private val teamMemberId = 12
    private val firstName = "firstname"
    private val lastName = "ln"
    private val password = "whatever"
    private val email = "this@camail.com"
    private val phoneNumber = "55965"
    private val eventId = 7

    private val mockUser = Account(firstName, lastName, password, email, phoneNumber)
    private val mockTeamMember = TeamMember(mutableSetOf(TeamMember.Role.ADMIN), mockTeam, "", teamMemberId)

    private val eventRepoMock: EventRepository = mock()
    private val placeRepoMock: PlaceRepository = mock()
    private val teamRepoMock: TeamRepository = mock()
    private val teamMemberRepoMock: TeamMemberRepository = mock()
    private val emailServiceMock: EmailService = mock()
    private val propertiesMock = EventsProperties(daysBefore = 7)
    private val eventService = EventService(eventRepoMock, teamMemberRepoMock, placeRepoMock, teamRepoMock, emailServiceMock, propertiesMock)

    @Test
    fun `create event with an empty name should be forbidden`() {
        val fromDateTime = LocalDateTime.of(2018, 1, 1, 0, 0)
        val toDateTime = LocalDateTime.of(2018, 1, 1, 10, 0)
        val dto = EventCreationDto(
                name = "",
                fromDate = fromDateTime.toLocalDate(),
                toDate = toDateTime.toLocalDate(),
                fromTime = fromDateTime.toLocalTime(),
                toTime = toDateTime.toLocalTime(),
                placeId = mockPlace.id!!,
                type = AbstractEvent.EventType.OTHER)
        assert {
            eventService.createEvent(teamId, dto)
        }.thrownError {
            isInstanceOf(BadParameterException::class)
            message().isEqualTo("Name of the event must not be empty")
        }
    }

    @Test
    fun `create punctual event should work`() {
        whenever(teamRepoMock.findById(any())).thenReturn(Optional.of(mockTeam))
        whenever(placeRepoMock.findById(any())).thenReturn(Optional.of(mockPlace))
        val fromDateTime = LocalDateTime.of(2018, 1, 1, 0, 0)
        val toDateTime = LocalDateTime.of(2018, 1, 1, 10, 0)
        val dto = EventCreationDto(
                name = "event",
                fromDate = fromDateTime.toLocalDate(),
                toDate = toDateTime.toLocalDate(),
                fromTime = fromDateTime.toLocalTime(),
                toTime = toDateTime.toLocalTime(),
                placeId = mockPlace.id!!,
                type = AbstractEvent.EventType.OTHER)
        eventService.createEvent(teamId, dto)
        argumentCaptor<Event>().apply {
            verify(eventRepoMock, times(1)).save(capture())
            assert(allValues).hasSize(1)
            assert(firstValue.name).isEqualTo("event")
            assert(firstValue.fromDateTime).isEqualTo(fromDateTime)
            assert(firstValue.toDateTime).isEqualTo(toDateTime)
            assert(firstValue.place).isEqualTo(mockPlace)
            assert(firstValue.team).isEqualTo(mockTeam)
        }
    }

    @Test
    fun `create recurrent event should produce one event`() {
        whenever(teamRepoMock.findById(any())).thenReturn(Optional.of(mockTeam))
        whenever(placeRepoMock.findById(any())).thenReturn(Optional.of(mockPlace))
        val fromDate = LocalDate.of(2018, 1, 1)
        val toDate = LocalDate.of(2018, 1, 8)
        val fromTime = LocalTime.of(10, 0)
        val toTime = LocalTime.of(11, 0)
        val dto = EventCreationDto(name = "event",
                fromDate = fromDate,
                toDate = toDate,
                fromTime = fromTime,
                toTime = toTime,
                placeId = mockPlace.id!!,
                isRecurrent = true,
                recurrenceDays = mutableSetOf(DayOfWeek.WEDNESDAY),
                type = AbstractEvent.EventType.OTHER)
        eventService.createEvent(teamId, dto)
        argumentCaptor<List<Event>>().apply {
            verify(eventRepoMock, times(1)).saveAll(capture())
            assert(allValues).hasSize(1)
            assert(firstValue).hasSize(1)
            val event = firstValue[0]
            val expectedDate = LocalDate.of(2018, 1, 3)
            assert(event.name).isEqualTo("event")
            assert(event.fromDateTime).isEqualTo(LocalDateTime.of(expectedDate, fromTime))
            assert(event.toDateTime).isEqualTo(LocalDateTime.of(expectedDate, toTime))
            assert(event.place).isEqualTo(mockPlace)
            assert(event.team).isEqualTo(mockTeam)
        }
    }

    @Test
    fun `create recurrent event should produce several events`() {
        whenever(teamRepoMock.findById(any())).thenReturn(Optional.of(mockTeam))
        whenever(placeRepoMock.findById(any())).thenReturn(Optional.of(mockPlace))
        val fromDate = LocalDate.of(2018, 1, 5) // It's a friday
        val toDate = LocalDate.of(2018, 3, 31)
        val fromTime = LocalTime.of(10, 0)
        val toTime = LocalTime.of(11, 0)
        val dto = EventCreationDto(
                name = "event",
                fromDate = fromDate,
                toDate = toDate,
                fromTime = fromTime,
                toTime = toTime,
                placeId = mockPlace.id!!,
                isRecurrent = true,
                recurrenceDays = mutableSetOf(DayOfWeek.WEDNESDAY, DayOfWeek.TUESDAY),
                type = AbstractEvent.EventType.OTHER)
        eventService.createEvent(teamId, dto)
        argumentCaptor<List<Event>>().apply {
            verify(eventRepoMock, times(1)).saveAll(capture())
            assert(allValues).hasSize(1)
            assert(firstValue).hasSize(24)
            val event = firstValue[0]
            val expectedDate = LocalDate.of(2018, 1, 9)
            assert(event.name).isEqualTo("event")
            assert(event.fromDateTime).isEqualTo(LocalDateTime.of(expectedDate, fromTime))
            assert(event.toDateTime).isEqualTo(LocalDateTime.of(expectedDate, toTime))
            assert(event.place).isEqualTo(mockPlace)
            assert(event.team).isEqualTo(mockTeam)
        }
    }

    @Test
    fun `user that does not exist trying to participate to an event should not be possible`() {
        whenever(eventRepoMock.findById(any())).thenReturn(Optional.of(mockEvent))
        whenever(teamMemberRepoMock.findByUsername(any(), any())).thenReturn(null)
        assert {
            eventService.participate(username, eventId, true, LocalDateTime.of(2018, 1, 30, 10, 0))
        }.thrownError { isInstanceOf(NotFoundException::class) }
    }

    @Test
    fun `user trying to participate to an event that does not exist should not be possible`() {
        whenever(teamMemberRepoMock.findByUsername(username, teamId)).thenReturn(mockTeamMember)
        whenever(eventRepoMock.findById(any())).thenReturn(Optional.empty())
        assert {
            eventService.participate(username, eventId, true, LocalDateTime.of(2018, 1, 30, 10, 0))
        }.thrownError { isInstanceOf(NotFoundException::class) }
    }

    @Test
    fun `user trying to participate to a past event should not be possible`() {
        whenever(teamMemberRepoMock.findByUsername(username, teamId)).thenReturn(mockTeamMember)
        whenever(eventRepoMock.findById(any())).thenReturn(Optional.of(mockEvent))
        assert {
            eventService.participate(username, eventId, true, LocalDateTime.of(2018, 2, 1, 10, 0))
        }.thrownError { isInstanceOf(SubscriptionNotPermittedException::class) }
    }

    @Test
    fun `user trying to participate to a future event more than x days before should not be possible`() {
        mockEvent.id = eventId
        mockTeamMember.account = mockUser
        whenever(teamMemberRepoMock.findByUsername(username, teamId)).thenReturn(mockTeamMember)
        whenever(eventRepoMock.findById(any())).thenReturn(Optional.of(mockEvent))
        whenever(eventRepoMock.save(mockEvent)).thenReturn(mockEvent)
        whenever(teamMemberRepoMock.findByUsername(username, teamId)).thenReturn(mockTeamMember)
        assert {
            eventService.participate(username, eventId, true, LocalDateTime.of(2018, 1, 20, 10, 0))
        }.thrownError { isInstanceOf(SubscriptionNotPermittedException::class) }
    }

    @Test
    fun `user trying to participate to a future event should add him in the present members`() {
        mockEvent.id = eventId
        mockTeamMember.account = mockUser
        whenever(teamMemberRepoMock.findByUsername(username, teamId)).thenReturn(mockTeamMember)
        whenever(eventRepoMock.findById(any())).thenReturn(Optional.of(mockEvent))
        whenever(eventRepoMock.save(mockEvent)).thenReturn(mockEvent)
        whenever(teamMemberRepoMock.findByUsername(username, teamId)).thenReturn(mockTeamMember)
        val eventDto = eventService.participate(username, eventId, true, LocalDateTime.of(2018, 1, 30, 10, 0))
        assert(mockEvent.presentMembers).hasSize(1)
        assert(mockEvent.absentMembers).hasSize(0)
        assert(mockEvent.waitingMembers).hasSize(0)
        verify(eventRepoMock, times(1)).save(mockEvent)
        assert(eventDto.name).isEqualTo(mockEvent.name)
        assert(eventDto._id).isEqualTo(mockEvent.id)
        assert(eventDto.absentMembers).isEmpty()
        assert(eventDto.waitingMembers).isEmpty()
        assert(eventDto.presentMembers).containsExactly(mockTeamMember.toDto())
        assert(eventDto.fromDateTime).isEqualTo(mockEvent.fromDateTime)
        assert(eventDto.toDateTime).isEqualTo(mockEvent.toDateTime)
        assert(eventDto.placeId).isEqualTo(mockEvent.place.id)
        assert(eventDto.isDone).isNull()
        assert(eventDto.localTeamName).isNull()
        assert(eventDto.localTeamScore).isNull()
        assert(eventDto.localTeamImgUrl).isNull()
        assert(eventDto.visitorTeamImgUrl).isNull()
        assert(eventDto.visitorTeamName).isNull()
        assert(eventDto.visitorTeamScore).isNull()
    }

    @Test
    fun `user trying to participate to a future event already full should add him in the waiting members`() {
        mockEvent.id = eventId
        mockEvent.maxMembers = 0
        mockTeamMember.account = mockUser
        whenever(teamMemberRepoMock.findByUsername(username, teamId)).thenReturn(mockTeamMember)
        whenever(eventRepoMock.findById(any())).thenReturn(Optional.of(mockEvent))
        whenever(eventRepoMock.save(mockEvent)).thenReturn(mockEvent)
        whenever(teamMemberRepoMock.findByUsername(username, teamId)).thenReturn(mockTeamMember)
        assert(mockEvent.presentMembers).hasSize(0)
        val eventDto = eventService.participate(username, eventId, true, LocalDateTime.of(2018, 1, 30, 10, 0))
        assert(mockEvent.presentMembers).hasSize(0)
        assert(mockEvent.absentMembers).hasSize(0)
        assert(mockEvent.waitingMembers).hasSize(1)
        verify(eventRepoMock, times(1)).save(mockEvent)
        assert(eventDto.name).isEqualTo(mockEvent.name)
        assert(eventDto._id).isEqualTo(mockEvent.id)
        assert(eventDto.absentMembers).isEmpty()
        assert(eventDto.presentMembers).isEmpty()
        assert(eventDto.waitingMembers).containsExactly(mockTeamMember.toDto())
        assert(eventDto.fromDateTime).isEqualTo(mockEvent.fromDateTime)
        assert(eventDto.toDateTime).isEqualTo(mockEvent.toDateTime)
        assert(eventDto.placeId).isEqualTo(mockEvent.place.id)
        assert(eventDto.isDone).isNull()
        assert(eventDto.localTeamName).isNull()
        assert(eventDto.localTeamScore).isNull()
        assert(eventDto.localTeamImgUrl).isNull()
        assert(eventDto.visitorTeamImgUrl).isNull()
        assert(eventDto.visitorTeamName).isNull()
        assert(eventDto.visitorTeamScore).isNull()
    }

    @Test
    fun `trying to update a past event should fail`() {
        whenever(eventRepoMock.findById(any())).thenReturn(Optional.of(mockEvent))
        whenever(placeRepoMock.findById(any())).thenReturn(Optional.of(mockPlace))
        whenever(eventRepoMock.save<Event>(any())).thenAnswer { it.arguments[0] }
        val fromDateTime = LocalDateTime.of(2018, 8, 1, 20, 0)
        val toDateTime = LocalDateTime.of(2018, 8, 1, 22, 0)
        val comparedDateTime = LocalDateTime.of(2018, 8, 1, 10, 0)
        val eventModifDto = EventModificationDto(
                fromDate = fromDateTime.toLocalDate(),
                fromTime = fromDateTime.toLocalTime(),
                toDate = toDateTime.toLocalDate(),
                toTime = toDateTime.toLocalTime(),
                placeId = placeId
        )
        assert {
            eventService.updateEvent(mockEvent.id, eventModifDto, comparedDateTime)
        }.thrownError {
            isInstanceOf(NotPossibleException::class)
            message().isEqualTo("Event has already occurred")
        }
    }

    @Test
    fun `trying to set a start date greater than an end date should fail`() {
        whenever(eventRepoMock.findById(any())).thenReturn(Optional.of(mockEvent))
        whenever(placeRepoMock.findById(any())).thenReturn(Optional.of(mockPlace))
        whenever(eventRepoMock.save<Event>(any())).thenAnswer { it.arguments[0] }
        val fromDateTime = LocalDateTime.of(2018, 1, 1, 23, 0)
        val toDateTime = LocalDateTime.of(2018, 1, 1, 21, 0)
        val comparedDateTime = LocalDateTime.of(2018, 1, 1, 10, 0)
        val eventModifDto = EventModificationDto(
                fromDate = fromDateTime.toLocalDate(),
                fromTime = fromDateTime.toLocalTime(),
                toDate = toDateTime.toLocalDate(),
                toTime = toDateTime.toLocalTime(),
                placeId = placeId
        )
        assert {
            eventService.updateEvent(mockEvent.id, eventModifDto, comparedDateTime)
        }.thrownError {
            isInstanceOf(NotPossibleException::class)
            message().isEqualTo("To date must be greater than from date")
        }
    }

    @Test
    fun `trying to set a from date in the past should fail`() {
        whenever(eventRepoMock.findById(any())).thenReturn(Optional.of(mockEvent))
        whenever(placeRepoMock.findById(any())).thenReturn(Optional.of(mockPlace))
        whenever(eventRepoMock.save<Event>(any())).thenAnswer { it.arguments[0] }
        val fromDateTime = LocalDateTime.of(2018, 1, 1, 20, 0)
        val toDateTime = LocalDateTime.of(2018, 1, 1, 22, 0)
        val comparedDateTime = LocalDateTime.of(2018, 1, 2, 0, 0)
        val eventModifDto = EventModificationDto(
                fromDate = fromDateTime.toLocalDate(),
                fromTime = fromDateTime.toLocalTime(),
                toDate = toDateTime.toLocalDate(),
                toTime = toDateTime.toLocalTime(),
                placeId = placeId
        )
        assert {
            eventService.updateEvent(mockEvent.id, eventModifDto, comparedDateTime)
        }.thrownError {
            isInstanceOf(NotPossibleException::class)
            message().isEqualTo("From date can not be in the past")
        }
    }

    @Test
    fun `trying to update an event should work`() {
        whenever(eventRepoMock.findById(any())).thenReturn(Optional.of(mockEvent))
        whenever(placeRepoMock.findById(any())).thenReturn(Optional.of(mockPlace))
        whenever(eventRepoMock.save<Event>(any())).thenAnswer { it.arguments[0] }
        val fromDateTime = LocalDateTime.of(2018, 1, 1, 20, 0)
        val toDateTime = LocalDateTime.of(2018, 1, 1, 22, 0)
        val comparedDateTime = LocalDateTime.of(2018, 1, 1, 10, 0)
        val eventModifDto = EventModificationDto(
                fromDate = fromDateTime.toLocalDate(),
                fromTime = fromDateTime.toLocalTime(),
                toDate = toDateTime.toLocalDate(),
                toTime = toDateTime.toLocalTime(),
                placeId = placeId
        )
        val updatedDto = eventService.updateEvent(mockEvent.id, eventModifDto, comparedDateTime)
        assert(updatedDto.fromDateTime).isEqualTo(fromDateTime)
        assert(updatedDto.toDateTime).isEqualTo(toDateTime)
    }
}