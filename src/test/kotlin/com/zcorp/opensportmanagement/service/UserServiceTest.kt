package com.zcorp.opensportmanagement.service

import com.nhaarman.mockito_kotlin.*
import com.zcorp.opensportmanagement.model.Event
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.model.TeamMember
import com.zcorp.opensportmanagement.model.User
import com.zcorp.opensportmanagement.repositories.EventRepository
import com.zcorp.opensportmanagement.repositories.TeamMemberRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.repositories.UserRepository
import com.zcorp.opensportmanagement.rest.NotFoundException
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime


class UserServiceTest {
    private lateinit var userService: UserService
    private lateinit var teamRepoMock: TeamRepository
    private lateinit var teamMemberRepoMock: TeamMemberRepository
    private lateinit var eventRepoMock: EventRepository
    private lateinit var userRepoMock: UserRepository
    private val teamMemberId = 12
    private val teamId = 5
    private val eventId = 7
    private val username = "username"
    private val firstName = "firstname"
    private val lastName = "ln"
    private val password = "whatever"
    private val email = "this@camail.com"
    private val phoneNumber = "55965"

    private val mockUser = User(username, firstName, lastName, password, email, phoneNumber)
    private val mockTeam = Team("SuperNam", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "", teamId)
    private val mockEvent = Event.Builder().name("TheOne")
            .fromDate(LocalDateTime.of(2018, 1, 1, 10, 0, 0))
            .toDate(LocalDateTime.of(2018, 1, 1, 11, 0, 0))
            .place("here")
            .team(mockTeam)
            .build()
    private val mockTeamMember = TeamMember(mutableSetOf(TeamMember.Role.ADMIN), mockTeam, "", teamMemberId)


    @Before
    fun setUp() {
        userRepoMock = mock()
        teamMemberRepoMock = mock()
        teamRepoMock = mock()
        eventRepoMock = mock()
        userService = UserService(teamRepoMock, teamMemberRepoMock, eventRepoMock, userRepoMock, BCryptPasswordEncoder())
    }

    @Test
    fun findUserNotExisting() {
        whenever(userRepoMock.findByUsername(any())).thenReturn(null)
        val user = userService.findByUsername("Foo")
        assertNull(user)
    }

    @Test
    fun findUserExisting() {
        whenever(userRepoMock.findByUsername(mockUser.username)).thenReturn(mockUser)
        val user = userService.findByUsername(username)
        assertNotNull(user)
        assertEquals(username, user?.username)
        assertEquals(firstName, user?.firstName)
        assertEquals(lastName, user?.lastName)
        assertEquals(email, user?.email)
        assertEquals(phoneNumber, user?.phoneNumber)
    }

    @Test(expected = NotFoundException::class)
    fun userNotExistingJoinTeam() {
        whenever(userRepoMock.findByUsername(any())).thenReturn(null)
        userService.joinTeam("Foo", 1)
    }

    @Test(expected = javax.persistence.EntityNotFoundException::class)
    fun userJoinTeamNotExisting() {
        whenever(userRepoMock.findByUsername(mockUser.username)).thenReturn(mockUser)
        whenever(teamRepoMock.getOne(any())).thenThrow(javax.persistence.EntityNotFoundException())
        userService.joinTeam(username, 1)
    }

    @Test
    fun userJoinTeam() {
        whenever(userRepoMock.findByUsername(mockUser.username)).thenReturn(mockUser)
        whenever(teamRepoMock.getOne(any())).thenReturn(mockTeam)
        assertEquals(0, mockUser.getMemberOf().size)
        userService.joinTeam(username, teamId)
        assertEquals(mockUser.getMemberOf().size, 1)
        verify(userRepoMock, times(1)).save(mockUser)
    }

    @Test(expected = NotFoundException::class)
    fun userNotExistingParticipateEvent() {
        whenever(eventRepoMock.getOne(any())).thenReturn(mockEvent)
        whenever(userRepoMock.findByUsername(any())).thenReturn(null)
        userService.participate("Foo", eventId, true)
    }

    @Test(expected = javax.persistence.EntityNotFoundException::class)
    fun userParticipateEventNotExisting() {
        whenever(userRepoMock.findByUsername(mockUser.username)).thenReturn(mockUser)
        whenever(eventRepoMock.getOne(any())).thenThrow(javax.persistence.EntityNotFoundException())
        userService.participate(username, eventId, true)
    }

    @Test
    fun userParticipateEvent() {
        mockEvent.id = eventId
        mockTeamMember.user = mockUser
        whenever(userRepoMock.findByUsername(mockUser.username)).thenReturn(mockUser)
        whenever(eventRepoMock.getOne(any())).thenReturn(mockEvent)
        whenever(eventRepoMock.save(mockEvent)).thenReturn(mockEvent)
        whenever(teamMemberRepoMock.findByUsername(mockUser.username, teamId)).thenReturn(mockTeamMember)
        val eventDto = userService.participate(username, eventId, true)
        assertEquals(1, mockEvent.getPresentMembers().size)
        assertEquals(0, mockEvent.getAbsentMembers().size)
        assertEquals(0, mockEvent.getWaitingMembers().size)
        verify(eventRepoMock, times(1)).save(mockEvent)
        assertEquals(mockEvent.name, eventDto.name)
        assertEquals(mockEvent.id, eventDto._id)
        assertEquals(emptyList<TeamMember>(), eventDto.absentMembers)
        assertEquals(emptyList<TeamMember>(), eventDto.waitingMembers)
        assertEquals(listOf(mockTeamMember.toDto()), eventDto.presentMembers)
        assertEquals(mockEvent.fromDateTime, eventDto.fromDate)
        assertEquals(mockEvent.toDateTime, eventDto.toDate)
        assertEquals(mockEvent.place, eventDto.place)
        assertEquals(null, eventDto.isDone)
        assertEquals(null, eventDto.localTeamName)
        assertEquals(null, eventDto.localTeamScore)
        assertEquals(null, eventDto.localTeamImgUrl)
        assertEquals(null, eventDto.visitorTeamImgUrl)
        assertEquals(null, eventDto.visitorTeamName)
        assertEquals(null, eventDto.visitorTeamScore)
    }

    @Test
    fun userParticipateEventFull() {
        mockEvent.id = eventId
        mockEvent.maxMembers = 0
        mockTeamMember.user = mockUser
        whenever(userRepoMock.findByUsername(mockUser.username)).thenReturn(mockUser)
        whenever(eventRepoMock.getOne(any())).thenReturn(mockEvent)
        whenever(eventRepoMock.save(mockEvent)).thenReturn(mockEvent)
        whenever(teamMemberRepoMock.findByUsername(mockUser.username, teamId)).thenReturn(mockTeamMember)
        assertEquals(mockEvent.getPresentMembers().size, 0)
        val eventDto = userService.participate(username, eventId, true)
        assertEquals(mockEvent.getPresentMembers().size, 0)
        assertEquals(mockEvent.getAbsentMembers().size, 0)
        assertEquals(mockEvent.getWaitingMembers().size, 1)
        verify(eventRepoMock, times(1)).save(mockEvent)
        assertEquals(mockEvent.name, eventDto.name)
        assertEquals(mockEvent.id, eventDto._id)
        assertEquals(emptyList<TeamMember>(), eventDto.absentMembers)
        assertEquals(emptyList<TeamMember>(), eventDto.presentMembers)
        assertEquals(listOf(mockTeamMember.toDto()), eventDto.waitingMembers)
        assertEquals(mockEvent.fromDateTime, eventDto.fromDate)
        assertEquals(mockEvent.toDateTime, eventDto.toDate)
        assertEquals(mockEvent.place, eventDto.place)
        assertEquals(null, eventDto.isDone)
        assertEquals(null, eventDto.localTeamName)
        assertEquals(null, eventDto.localTeamScore)
        assertEquals(null, eventDto.localTeamImgUrl)
        assertEquals(null, eventDto.visitorTeamImgUrl)
        assertEquals(null, eventDto.visitorTeamName)
        assertEquals(null, eventDto.visitorTeamScore)
    }}