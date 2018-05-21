package com.zcorp.opensportmanagement.service

import com.nhaarman.mockito_kotlin.*
import com.zcorp.opensportmanagement.dto.EventCreationDto
import com.zcorp.opensportmanagement.model.*
import com.zcorp.opensportmanagement.repositories.EventRepository
import com.zcorp.opensportmanagement.repositories.PlaceRepository
import com.zcorp.opensportmanagement.repositories.TeamMemberRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class EventServiceTest : StringSpec() {
    private val teamId = 5
    private val placeId = 1
    private val mockTeam = Team("SuperNam", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "", teamId)
    private val mockPlace = Place("The place", "", "Toulouse", mockTeam, placeId)
    private val mockEvent = Event.Builder().name("TheOne")
            .fromDate(LocalDateTime.now().plusMinutes(2L))
            .toDate(LocalDateTime.now().plusMinutes(5L))
            .place(mockPlace)
            .team(mockTeam)
            .build()
    private val teamMemberId = 12
    private val username = "username"
    private val firstName = "firstname"
    private val lastName = "ln"
    private val password = "whatever"
    private val email = "this@camail.com"
    private val phoneNumber = "55965"
    private val eventId = 7

    private val mockUser = User(username, firstName, lastName, password, email, phoneNumber)
    private val mockTeamMember = TeamMember(mutableSetOf(TeamMember.Role.ADMIN), mockTeam, "", teamMemberId)

    private val eventRepoMock: EventRepository = mock()
    private val placeRepoMock: PlaceRepository = mock()
    private val teamRepoMock: TeamRepository = mock()
    private val teamMemberRepoMock: TeamMemberRepository = mock()
    private val eventService = EventService(eventRepoMock, teamMemberRepoMock, placeRepoMock, teamRepoMock)

    override fun isInstancePerTest() = true

    init {
        "create punctual event should work" {
            whenever(teamRepoMock.getOne(any())).thenReturn(mockTeam)
            whenever(placeRepoMock.getOne(any())).thenReturn(mockPlace)
            val fromDateTime = LocalDateTime.of(2018, 1, 1, 0, 0)
            val toDateTime = LocalDateTime.of(2018, 1, 1, 10, 0)
            val dto = EventCreationDto("event", fromDateTime.toLocalDate(), toDateTime.toLocalDate(),
                    fromDateTime.toLocalTime(), toDateTime.toLocalTime(), mockPlace.id)
            eventService.createEvent(teamId, dto)
            argumentCaptor<Event>().apply {
                verify(eventRepoMock, times(1)).save(capture())
                allValues.size shouldBe 1
                firstValue.name shouldBe "event"
                firstValue.fromDateTime shouldBe fromDateTime
                firstValue.toDateTime shouldBe toDateTime
                firstValue.place shouldBe mockPlace
                firstValue.team shouldBe mockTeam
            }
        }

        "create recurrent event should produce one event" {
            whenever(teamRepoMock.getOne(any())).thenReturn(mockTeam)
            whenever(placeRepoMock.getOne(any())).thenReturn(mockPlace)
            val fromDate = LocalDate.of(2018, 1, 1)
            val toDate = LocalDate.of(2018, 1, 8)
            val fromTime = LocalTime.of(10, 0)
            val toTime = LocalTime.of(11, 0)
            val dto = EventCreationDto("event", fromDate, toDate, fromTime, toTime, mockPlace.id,
                    true, mutableSetOf(DayOfWeek.WEDNESDAY))
            eventService.createEvent(teamId, dto)
            argumentCaptor<List<Event>>().apply {
                verify(eventRepoMock, times(1)).saveAll(capture())
                allValues.size shouldBe 1
                firstValue.size shouldBe 1
                val event = firstValue[0]
                val expectedDate = LocalDate.of(2018, 1, 3)
                event.name shouldBe "event"
                event.fromDateTime shouldBe LocalDateTime.of(expectedDate, fromTime)
                event.toDateTime shouldBe LocalDateTime.of(expectedDate, toTime)
                event.place shouldBe mockPlace
                event.team shouldBe mockTeam
            }
        }

        "create recurrent event should produce several events" {
            whenever(teamRepoMock.getOne(any())).thenReturn(mockTeam)
            whenever(placeRepoMock.getOne(any())).thenReturn(mockPlace)
            val fromDate = LocalDate.of(2018, 1, 5) // It's a friday
            val toDate = LocalDate.of(2018, 3, 31)
            val fromTime = LocalTime.of(10, 0)
            val toTime = LocalTime.of(11, 0)
            val dto = EventCreationDto("event", fromDate, toDate, fromTime, toTime, mockPlace.id,
                    true, mutableSetOf(DayOfWeek.WEDNESDAY, DayOfWeek.TUESDAY))
            eventService.createEvent(teamId, dto)
            argumentCaptor<List<Event>>().apply {
                verify(eventRepoMock, times(1)).saveAll(capture())
                allValues.size shouldBe 1
                firstValue.size shouldBe 24
                val event = firstValue[0]
                val expectedDate = LocalDate.of(2018, 1, 9)
                event.name shouldBe "event"
                event.fromDateTime shouldBe LocalDateTime.of(expectedDate, fromTime)
                event.toDateTime shouldBe LocalDateTime.of(expectedDate, toTime)
                event.place shouldBe mockPlace
                event.team shouldBe mockTeam
            }
        }

        "user that does not exist trying to participate to an event should not be possible" {
            shouldThrow<NotFoundException> {
                whenever(eventRepoMock.getOne(any())).thenReturn(mockEvent)
                whenever(teamMemberRepoMock.findByUsername(any(), any())).thenReturn(null)
                eventService.participate("Foo", eventId, true)
            }
        }

        "user trying to participate to an event that does not exist should not be possible" {
            whenever(teamMemberRepoMock.findByUsername(mockUser.username, teamId)).thenReturn(mockTeamMember)
            whenever(eventRepoMock.getOne(any())).thenThrow(javax.persistence.EntityNotFoundException())
            shouldThrow<NotFoundException> {
                eventService.participate(username, eventId, true)
            }
        }

        "user trying to participate to a past event should not be possible" {
            val pastEvent = Event.Builder().name("TheOne")
                    .fromDate(LocalDateTime.of(2018, 1, 1, 10, 0, 0))
                    .toDate(LocalDateTime.of(2018, 1, 1, 11, 0, 0))
                    .place(mockPlace)
                    .team(mockTeam)
                    .build()
            whenever(teamMemberRepoMock.findByUsername(mockUser.username, teamId)).thenReturn(mockTeamMember)
            whenever(eventRepoMock.getOne(any())).thenReturn(pastEvent)
            shouldThrow<PastEventException> {
                eventService.participate(username, eventId, true)
            }
        }

        "user trying to participate to a future event should add him in the present members" {
            mockEvent.id = eventId
            mockTeamMember.user = mockUser
            whenever(teamMemberRepoMock.findByUsername(mockUser.username, teamId)).thenReturn(mockTeamMember)
            whenever(eventRepoMock.getOne(any())).thenReturn(mockEvent)
            whenever(eventRepoMock.save(mockEvent)).thenReturn(mockEvent)
            whenever(teamMemberRepoMock.findByUsername(mockUser.username, teamId)).thenReturn(mockTeamMember)
            val eventDto = eventService.participate(username, eventId, true)
            mockEvent.getPresentMembers().size shouldBe 1
            mockEvent.getAbsentMembers().size shouldBe 0
            mockEvent.getWaitingMembers().size shouldBe 0
            verify(eventRepoMock, times(1)).save(mockEvent)
            eventDto.name shouldBe mockEvent.name
            eventDto._id shouldBe mockEvent.id
            eventDto.absentMembers shouldBe emptyList<TeamMember>()
            eventDto.waitingMembers shouldBe emptyList<TeamMember>()
            eventDto.presentMembers shouldBe listOf(mockTeamMember.toDto())
            eventDto.fromDateTime shouldBe mockEvent.fromDateTime
            eventDto.toDateTime shouldBe mockEvent.toDateTime
            eventDto.placeId shouldBe mockEvent.place.id
            eventDto.isDone shouldBe null
            eventDto.localTeamName shouldBe null
            eventDto.localTeamScore shouldBe null
            eventDto.localTeamImgUrl shouldBe null
            eventDto.visitorTeamImgUrl shouldBe null
            eventDto.visitorTeamName shouldBe null
            eventDto.visitorTeamScore shouldBe null
        }

        "user trying to participate to a future event already full should add him in the waiting members" {
            mockEvent.id = eventId
            mockEvent.maxMembers = 0
            mockTeamMember.user = mockUser
            whenever(teamMemberRepoMock.findByUsername(mockUser.username, teamId)).thenReturn(mockTeamMember)
            whenever(eventRepoMock.getOne(any())).thenReturn(mockEvent)
            whenever(eventRepoMock.save(mockEvent)).thenReturn(mockEvent)
            whenever(teamMemberRepoMock.findByUsername(mockUser.username, teamId)).thenReturn(mockTeamMember)
            mockEvent.getPresentMembers().size shouldBe 0
            val eventDto = eventService.participate(username, eventId, true)
            mockEvent.getPresentMembers().size shouldBe 0
            mockEvent.getAbsentMembers().size shouldBe 0
            mockEvent.getWaitingMembers().size shouldBe 1
            verify(eventRepoMock, times(1)).save(mockEvent)
            eventDto.name shouldBe mockEvent.name
            eventDto._id shouldBe mockEvent.id
            eventDto.absentMembers shouldBe emptyList<TeamMember>()
            eventDto.presentMembers shouldBe emptyList<TeamMember>()
            eventDto.waitingMembers shouldBe listOf(mockTeamMember.toDto())
            eventDto.fromDateTime shouldBe mockEvent.fromDateTime
            eventDto.toDateTime shouldBe mockEvent.toDateTime
            eventDto.placeId shouldBe mockEvent.place.id
            eventDto.isDone shouldBe null
            eventDto.localTeamName shouldBe null
            eventDto.localTeamScore shouldBe null
            eventDto.localTeamImgUrl shouldBe null
            eventDto.visitorTeamImgUrl shouldBe null
            eventDto.visitorTeamName shouldBe null
            eventDto.visitorTeamScore shouldBe null
        }
    }
}