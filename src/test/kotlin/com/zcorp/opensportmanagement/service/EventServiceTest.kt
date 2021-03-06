package com.zcorp.opensportmanagement.service

import assertk.assert
import assertk.assertAll
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import assertk.assertions.message
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.config.EventsProperties
import com.zcorp.opensportmanagement.dto.EventCreationDto
import com.zcorp.opensportmanagement.dto.EventModificationDto
import com.zcorp.opensportmanagement.model.AbstractEvent
import com.zcorp.opensportmanagement.model.Account
import com.zcorp.opensportmanagement.model.Event
import com.zcorp.opensportmanagement.model.MemberResponse
import com.zcorp.opensportmanagement.model.Place
import com.zcorp.opensportmanagement.model.Place.PlaceType
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.model.TeamMember
import com.zcorp.opensportmanagement.repository.EventRepository
import com.zcorp.opensportmanagement.repository.MemberResponseRepository
import com.zcorp.opensportmanagement.repository.PlaceRepository
import com.zcorp.opensportmanagement.repository.TeamMemberRepository
import com.zcorp.opensportmanagement.repository.TeamRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Optional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventServiceTest {
    private val username = "foo"
    private val teamId = 5
    private val placeId = 1
    private val teamMemberId = 12
    private val teamMember2Id = 45
    private val teamMember3Id = 76
    private val teamMember4Id = 127
    private val firstName = "firstname"
    private val lastName = "ln"
    private val password = "whatever"
    private val phoneNumber = "55965"
    private val eventId = 7
    private val cancelledEventId = 17

    private val mockTeam = Team("SuperNam", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "", teamId)
    private val mockPlace = Place("The place", "", "Toulouse", PlaceType.STADIUM, mockTeam, placeId)
    private val mockEvent: AbstractEvent
    private val cancelledMockEvent: AbstractEvent
    private val mockUser = Account(firstName = firstName,
            lastName = lastName,
            password = password,
            email = "this@camail.com",
            phoneNumber = phoneNumber,
            username = username)
    private val mockUser2 = Account(firstName = "second_firstName",
            lastName = "second_lastName",
            password = password,
            email = "second_email@camail.com",
            phoneNumber = phoneNumber,
            username = "foo2")
    private val mockUser3 = Account(firstName = "third_firstName",
            lastName = "third_lastName",
            password = password,
            email = "third_email@camail.com",
            phoneNumber = phoneNumber,
            username = "foo3")
    private val mockUser4 = Account(firstName = "fourth_firstName",
            lastName = "fourth_lastName",
            password = password,
            email = "",
            phoneNumber = phoneNumber,
            username = "foo4")
    private val mockTeamMember = TeamMember(mutableSetOf(TeamMember.Role.ADMIN), mockTeam, mockUser, "", teamMemberId)
    private val mockTeamMember2 = TeamMember(mutableSetOf(TeamMember.Role.ADMIN), mockTeam, mockUser2, "", teamMember2Id)
    private val mockTeamMember3 = TeamMember(mutableSetOf(TeamMember.Role.ADMIN), mockTeam, mockUser3, "", teamMember3Id)
    private val mockTeamMember4 = TeamMember(mutableSetOf(TeamMember.Role.PLAYER), mockTeam, mockUser4, "", teamMember4Id)

    private val eventRepoMock: EventRepository = mock()
    private val placeRepoMock: PlaceRepository = mock()
    private val teamRepoMock: TeamRepository = mock()
    private val teamMemberRepoMock: TeamMemberRepository = mock()
    private val memberResponseRepoMock: MemberResponseRepository = mock()
    private val emailServiceMock: EmailService = mock()
    private val propertiesMock = EventsProperties(daysBefore = 7)
    private val eventService = EventService(eventRepository = eventRepoMock,
            memberResponseRepository = memberResponseRepoMock,
            teamMemberRepository = teamMemberRepoMock,
            placeRepository = placeRepoMock,
            teamRepository = teamRepoMock,
            emailService = emailServiceMock,
            properties = propertiesMock
    )

    init {
        mockEvent = Event.Builder()
                .name("TheOne")
                .fromDate(LocalDateTime.of(2018, 1, 31, 10, 0))
                .toDate(LocalDateTime.of(2018, 1, 31, 12, 0))
                .place(mockPlace)
                .team(mockTeam)
                .build()
        mockEvent.id = eventId
        cancelledMockEvent = Event.Builder()
                .name("TheCancelledOne")
                .fromDate(LocalDateTime.of(2018, 1, 31, 10, 0))
                .toDate(LocalDateTime.of(2018, 1, 31, 12, 0))
                .place(mockPlace)
                .team(mockTeam)
                .build()
        cancelledMockEvent.id = cancelledEventId
    }

    @BeforeEach
    fun init() {
        reset(eventRepoMock,
                placeRepoMock,
                teamRepoMock,
                teamMemberRepoMock,
                memberResponseRepoMock,
                emailServiceMock)
        mockEvent.cancelled = false
        mockEvent.openForRegistration = false

        cancelledMockEvent.cancelled = true
        cancelledMockEvent.openForRegistration = false
        mockEvent.maxMembers = 20
        cancelledMockEvent.maxMembers = 20
        mockEvent.membersResponse.clear()
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
    fun `user trying to participate to a future event should add him in the present members`() {
        mockEvent.openForRegistration = true
        whenever(teamMemberRepoMock.findByUsername(username, teamId)).thenReturn(mockTeamMember)
        whenever(eventRepoMock.findById(any())).thenReturn(Optional.of(mockEvent))
        whenever(eventRepoMock.save(mockEvent)).thenReturn(mockEvent)
        whenever(teamMemberRepoMock.findByUsername(username, teamId)).thenReturn(mockTeamMember)
        whenever(memberResponseRepoMock.save<MemberResponse>(any())).thenAnswer { it.arguments[0] }
        eventService.participate(username, eventId, true, LocalDateTime.of(2018, 1, 30, 10, 0))
        argumentCaptor<MemberResponse>().apply {
            verify(memberResponseRepoMock, times(1)).save(capture())
            assert(allValues).hasSize(1)
            assert(firstValue.event).isEqualTo(mockEvent)
            assert(firstValue.teamMember).isEqualTo(mockTeamMember)
            assert(firstValue.status).isEqualTo(MemberResponse.Status.PRESENT)
        }
    }

    @Test
    fun `user trying to participate to a future event already full should add him in the waiting members`() {
        mockEvent.maxMembers = 0
        mockEvent.openForRegistration = true
        whenever(teamMemberRepoMock.findByUsername(username, teamId)).thenReturn(mockTeamMember)
        whenever(eventRepoMock.findById(any())).thenReturn(Optional.of(mockEvent))
        whenever(eventRepoMock.save(mockEvent)).thenReturn(mockEvent)
        whenever(teamMemberRepoMock.findByUsername(username, teamId)).thenReturn(mockTeamMember)
        whenever(memberResponseRepoMock.save<MemberResponse>(any())).thenAnswer { it.arguments[0] }
        eventService.participate(username, eventId, true, LocalDateTime.of(2018, 1, 30, 10, 0))
        argumentCaptor<MemberResponse>().apply {
            verify(memberResponseRepoMock, times(1)).save(capture())
            assert(allValues).hasSize(1)
            assert(firstValue.event).isEqualTo(mockEvent)
            assert(firstValue.teamMember).isEqualTo(mockTeamMember)
            assert(firstValue.status).isEqualTo(MemberResponse.Status.WAITING)
        }
    }

    @Test
    fun `user withdrawing from an event already full should add him in the absent members and first waiting member should be present`() {
        mockEvent.maxMembers = 1
        mockEvent.openForRegistration = true
        mockEvent.membersResponse.add(MemberResponse(mockEvent, mockTeamMember, MemberResponse.Status.PRESENT, LocalDateTime.of(2018, 1, 1, 1, 0)))
        mockEvent.membersResponse.add(MemberResponse(mockEvent, mockTeamMember2, MemberResponse.Status.WAITING, LocalDateTime.of(2018, 1, 2, 1, 0)))
        mockEvent.membersResponse.add(MemberResponse(mockEvent, mockTeamMember3, MemberResponse.Status.WAITING, LocalDateTime.of(2018, 1, 1, 1, 0)))

        whenever(teamMemberRepoMock.findByUsername(username, teamId)).thenReturn(mockTeamMember)
        whenever(eventRepoMock.findById(any())).thenReturn(Optional.of(mockEvent))
        whenever(eventRepoMock.save(mockEvent)).thenReturn(mockEvent)
        whenever(teamMemberRepoMock.findByUsername(username, teamId)).thenReturn(mockTeamMember)
        whenever(memberResponseRepoMock.save<MemberResponse>(any())).thenAnswer { it.arguments[0] }
        eventService.participate(username, eventId, false, LocalDateTime.of(2018, 1, 30, 10, 0))
        argumentCaptor<MemberResponse>().apply {
            verify(memberResponseRepoMock, times(2)).save(capture())
            assertAll {
                assert(allValues).hasSize(2)
                assert(firstValue.teamMember).isEqualTo(mockTeamMember3)
                assert(firstValue.status).isEqualTo(MemberResponse.Status.PRESENT)
                assert(firstValue.event).isEqualTo(mockEvent)
                assert(secondValue.teamMember).isEqualTo(mockTeamMember)
                assert(secondValue.status).isEqualTo(MemberResponse.Status.ABSENT)
                assert(secondValue.event).isEqualTo(mockEvent)
            }
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

    @Test
    fun `cancel an event should work`() {
        whenever(eventRepoMock.findById(any())).thenReturn(Optional.of(mockEvent))
        whenever(eventRepoMock.save<Event>(any())).thenAnswer { it.arguments[0] }

        val comparedDateTime = LocalDateTime.of(2018, 1, 1, 20, 0)
        val dto = eventService.cancelEvent(mockEvent.id, comparedDateTime)
        argumentCaptor<Event>().apply {
            verify(eventRepoMock, times(1)).save(capture())
            assert(allValues).hasSize(1)
            assert(firstValue.cancelled).isEqualTo(true)
        }
        assert(dto.cancelled).isEqualTo(true)
    }

    @Test
    fun `get emails of members that have not responded yet`() {
        whenever(eventRepoMock.getMembersThatHaveNotResponded(eventId))
                .thenReturn(listOf(mockTeamMember, mockTeamMember3, mockTeamMember4))
        val emails = eventService.getMembersMailNotResponded(eventId)
        assert(emails).hasSize(2)
        assert(emails).containsExactly(mockTeamMember.account.email, mockTeamMember3.account.email)
    }

    @Test
    fun `open events should return non cancelled events`() {
        val comparedDate = LocalDateTime.of(2018, 1, 1, 0, 0)
        whenever(eventRepoMock.findByOpenForRegistrationFalseAndFromDateTimeBefore(comparedDate))
                .thenReturn(listOf(mockEvent, cancelledMockEvent))
        whenever(eventRepoMock.getMembersThatHaveNotResponded(mockEvent.id))
                .thenReturn(listOf(mockTeamMember))
        eventService.openEvents(comparedDate)
        argumentCaptor<List<String>>().apply {
            verify(emailServiceMock, times(1)).sendMessage(capture(), any(), any())
            assert(allValues).hasSize(1)
            assert(firstValue).containsExactly(mockTeamMember.account.email)
        }
        assert(mockEvent.openForRegistration).isTrue()
        assert(cancelledMockEvent.openForRegistration).isFalse()
    }

    @Test
    fun `open events should return no events`() {
        val comparedDate = LocalDateTime.of(2018, 1, 1, 0, 0)
        mockEvent.cancelled = true
        whenever(eventRepoMock.findByOpenForRegistrationFalseAndFromDateTimeBefore(comparedDate))
                .thenReturn(listOf(mockEvent, cancelledMockEvent))
        whenever(eventRepoMock.getMembersThatHaveNotResponded(mockEvent.id))
                .thenReturn(listOf(mockTeamMember))
        eventService.openEvents(comparedDate)
        argumentCaptor<List<String>>().apply {
            verify(emailServiceMock, times(0)).sendMessage(capture(), any(), any())
        }
        assert(mockEvent.openForRegistration).isFalse()
        assert(cancelledMockEvent.openForRegistration).isFalse()
    }

    @Nested
    inner class ErrorCases {

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
        fun `Participating to a cancelled event should not be possible`() {
            whenever(eventRepoMock.findById(any())).thenReturn(Optional.of(cancelledMockEvent))
            whenever(teamMemberRepoMock.findByUsername(any(), any())).thenReturn(mockTeamMember)
            assert {
                eventService.participate(username, cancelledEventId, true, LocalDateTime.of(2018, 1, 30, 10, 0))
            }.thrownError {
                isInstanceOf(SubscriptionNotPermittedException::class)
                message().isEqualTo("Event ${cancelledMockEvent.id} is cancelled")
            }
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
            whenever(teamMemberRepoMock.findByUsername(username, teamId)).thenReturn(mockTeamMember)
            whenever(eventRepoMock.findById(any())).thenReturn(Optional.of(mockEvent))
            whenever(eventRepoMock.save(mockEvent)).thenReturn(mockEvent)
            whenever(teamMemberRepoMock.findByUsername(username, teamId)).thenReturn(mockTeamMember)
            assert {
                eventService.participate(username, eventId, true, LocalDateTime.of(2018, 1, 20, 10, 0))
            }.thrownError { isInstanceOf(SubscriptionNotPermittedException::class) }
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
        fun `cancel a past event should fail`() {
            whenever(eventRepoMock.findById(any())).thenReturn(Optional.of(mockEvent))
            val comparedDateTime = LocalDateTime.of(2018, 2, 1, 20, 0)
            assert {
                eventService.cancelEvent(mockEvent.id, comparedDateTime)
            }.thrownError {
                isInstanceOf(NotPossibleException::class)
                message().isEqualTo("The event has already occurred")
            }
        }
    }
}