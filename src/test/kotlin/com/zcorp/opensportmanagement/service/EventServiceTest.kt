package com.zcorp.opensportmanagement.service

import com.nhaarman.mockito_kotlin.*
import com.zcorp.opensportmanagement.dto.EventCreationDto
import com.zcorp.opensportmanagement.model.Event
import com.zcorp.opensportmanagement.model.Stadium
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.repositories.EventRepository
import com.zcorp.opensportmanagement.repositories.StadiumRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


class EventServiceTest {
    private lateinit var eventService: EventService
    private lateinit var eventRepoMock: EventRepository
    private lateinit var stadiumRepoMock: StadiumRepository
    private lateinit var teamRepoMock: TeamRepository
    private val teamId = 5
    private val stadiumId = 1
    private val mockTeam = Team("SuperNam", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "", teamId)
    private val mockStadium = Stadium("The stadium", "", "Toulouse", mockTeam, stadiumId)

    @Before
    fun setUp() {
        eventRepoMock = mock()
        stadiumRepoMock = mock()
        teamRepoMock = mock()
        eventService = EventService(eventRepoMock, stadiumRepoMock, teamRepoMock)
    }

    @Test
    fun createPunctualEvent() {
        whenever(teamRepoMock.getOne(any())).thenReturn(mockTeam)
        whenever(stadiumRepoMock.getOne(any())).thenReturn(mockStadium)
        val fromDate = LocalDateTime.of(2018, 1, 1, 0, 0)
        val toDate = LocalDateTime.of(2018, 1, 1, 10, 0)
        val dto = EventCreationDto("event", fromDate, toDate, null, mockStadium.id, false,
                null, null, null, null, null)
        eventService.createEvent(teamId, dto)
        argumentCaptor<Event>().apply {
            verify(eventRepoMock, times(1)).save(capture())
            assertEquals(1, allValues.size)
            assertEquals("event", firstValue.name)
            assertEquals(fromDate, firstValue.fromDateTime)
            assertEquals(toDate, firstValue.toDateTime)
            assertEquals(null, firstValue.place)
            assertEquals(mockStadium, firstValue.stadium)
            assertEquals(mockTeam, firstValue.team)
        }
    }

    @Test
    fun createRecurrentEventOneDate() {
        whenever(teamRepoMock.getOne(any())).thenReturn(mockTeam)
        whenever(stadiumRepoMock.getOne(any())).thenReturn(mockStadium)
        val fromDate = LocalDate.of(2018, 1, 1)
        val toDate = LocalDate.of(2018, 1, 8)
        val fromTime = LocalTime.of(10, 0)
        val toTime = LocalTime.of(11, 0)
        val dto = EventCreationDto("event", null, null, null, mockStadium.id, true,
                mutableSetOf(DayOfWeek.WEDNESDAY), fromTime, toTime, fromDate, toDate)
        eventService.createEvent(teamId, dto)
        argumentCaptor<List<Event>>().apply {
            verify(eventRepoMock, times(1)).saveAll(capture())
            assertEquals(1, allValues.size)
            assertEquals(1, firstValue.size)
            val event = firstValue[0]
            val expectedDate = LocalDate.of(2018, 1, 3)
            assertEquals("event", event.name)
            assertEquals(LocalDateTime.of(expectedDate, fromTime), event.fromDateTime)
            assertEquals(LocalDateTime.of(expectedDate, toTime), event.toDateTime)
            assertEquals(null, event.place)
            assertEquals(mockStadium, event.stadium)
            assertEquals(mockTeam, event.team)
        }
    }

    @Test
    fun createRecurrentEventSeveralDates() {
        whenever(teamRepoMock.getOne(any())).thenReturn(mockTeam)
        whenever(stadiumRepoMock.getOne(any())).thenReturn(mockStadium)
        val fromDate = LocalDate.of(2018, 1, 5) // It's a friday
        val toDate = LocalDate.of(2018, 3, 31)
        val fromTime = LocalTime.of(10, 0)
        val toTime = LocalTime.of(11, 0)
        val dto = EventCreationDto("event", null, null, null, mockStadium.id, true,
                mutableSetOf(DayOfWeek.WEDNESDAY, DayOfWeek.TUESDAY), fromTime, toTime, fromDate, toDate)
        eventService.createEvent(teamId, dto)
        argumentCaptor<List<Event>>().apply {
            verify(eventRepoMock, times(1)).saveAll(capture())
            assertEquals(1, allValues.size)
            assertEquals(24, firstValue.size)
            val event = firstValue[0]
            val expectedDate = LocalDate.of(2018, 1, 9)
            assertEquals("event", event.name)
            assertEquals(LocalDateTime.of(expectedDate, fromTime), event.fromDateTime)
            assertEquals(LocalDateTime.of(expectedDate, toTime), event.toDateTime)
            assertEquals(null, event.place)
            assertEquals(mockStadium, event.stadium)
            assertEquals(mockTeam, event.team)
        }
    }

}