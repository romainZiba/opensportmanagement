package com.zcorp.opensportmanagement.repository

import assertk.assert
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isNotEmpty
import com.zcorp.opensportmanagement.model.Account
import com.zcorp.opensportmanagement.model.Event
import com.zcorp.opensportmanagement.model.MemberResponse
import com.zcorp.opensportmanagement.model.Place
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.model.TeamMember
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@DataJpaTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class EventRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var eventRepository: EventRepository

    private lateinit var savedTeamMember1: TeamMember
    private lateinit var savedTeamMember2: TeamMember
    private lateinit var savedPlace: Place
    private lateinit var savedTeam: Team

    @BeforeEach
    fun init() {
        // Given a team
        val team = Team("", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "")
        savedTeam = entityManager.persist(team)

        // Given accounts
        val account1 = Account("first", "last", "pass", "email1", "")
        val account2 = Account("second", "last", "pass", "email2", "")
        val savedAccount1 = entityManager.persist(account1)
        val savedAccount2 = entityManager.persist(account2)

        // Given team members
        val teamMember1 = TeamMember(mutableSetOf(), savedTeam, "")
        val teamMember2 = TeamMember(mutableSetOf(), savedTeam, "")
        teamMember1.account = savedAccount1
        teamMember2.account = savedAccount2
        savedTeamMember1 = entityManager.persist(teamMember1)
        savedTeamMember2 = entityManager.persist(teamMember2)

        // Given a place
        val place = Place("The stadium", "", "NYC", Place.PlaceType.STADIUM, savedTeam)
        savedPlace = entityManager.persist(place)
    }

    @Test
    fun `should return team member that has not responded`() {
        val event = Event.Builder()
                .name("event")
                .team(savedTeam)
                .fromDate(LocalDateTime.of(2019, 1, 1, 10, 0))
                .place(savedPlace)
                .build()

        val savedEvent = entityManager.persistAndFlush(event)
        entityManager.persist(MemberResponse(savedEvent, savedTeamMember2, MemberResponse.Status.ABSENT))

        val teamMembersToNotify = eventRepository.getMembersThatHaveNotResponded(event.id)
        assert(teamMembersToNotify).isNotEmpty()
        assert(teamMembersToNotify).containsExactly(savedTeamMember1)
    }

    @Test
    fun `should return empty list when all team members have responded`() {
        val event = Event.Builder()
                .name("event")
                .team(savedTeam)
                .fromDate(LocalDateTime.of(2019, 1, 1, 10, 0))
                .place(savedPlace)
                .build()
        val savedEvent = entityManager.persistAndFlush(event)
        entityManager.persist(MemberResponse(savedEvent, savedTeamMember1, MemberResponse.Status.PRESENT))
        entityManager.persist(MemberResponse(savedEvent, savedTeamMember2, MemberResponse.Status.ABSENT))
        val teamMembersToNotify = eventRepository.getMembersThatHaveNotResponded(savedEvent.id)
        assert(teamMembersToNotify).isEmpty()
    }

    @Test
    fun `should return list containing all team members when none has responded`() {
        val event = Event.Builder()
                .name("event")
                .team(savedTeam)
                .fromDate(LocalDateTime.of(2019, 1, 1, 10, 0))
                .place(savedPlace)
                .build()
        val savedEvent = entityManager.persistAndFlush(event)
        val teamMembersToNotify = eventRepository.getMembersThatHaveNotResponded(savedEvent.id)
        assert(teamMembersToNotify).isEmpty()
    }
}