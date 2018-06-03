package com.zcorp.opensportmanagement.repository

import assertk.assert
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isNotEmpty
import com.zcorp.opensportmanagement.model.Event
import com.zcorp.opensportmanagement.model.Place
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.model.TeamMember
import com.zcorp.opensportmanagement.model.Account
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@DataJpaTest
@ActiveProfiles("test")
open class EventRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var eventRepository: EventRepository

    @Test
    fun `should return team member that has not responded`() {
        // given
        val team = Team("", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "")
        val savedTeam = entityManager.persist(team)

        val user1 = Account("user1", "first", "last", "pass", "email1", "")
        val user2 = Account("user2", "second", "last", "pass", "email2", "")
        val savedUser1 = entityManager.persist(user1)
        val savedUser2 = entityManager.persist(user2)

        val teamMember1 = TeamMember(mutableSetOf(), savedTeam, "")
        val teamMember2 = TeamMember(mutableSetOf(), savedTeam, "")
        teamMember1.account = savedUser1
        teamMember2.account = savedUser2
        val savedTeamMember1 = entityManager.persist(teamMember1)
        val savedTeamMember2 = entityManager.persist(teamMember2)

        val place = Place("The stadium", "", "NYC", Place.PlaceType.STADIUM, savedTeam)
        val savedPlace = entityManager.persist(place)

        val event = Event.Builder()
                .name("event")
                .team(savedTeam)
                .fromDate(LocalDateTime.of(2019, 1, 1, 10, 0))
                .place(savedPlace)
                .build()
        event.absentMembers.add(savedTeamMember2)
        val savedEvent = entityManager.persistAndFlush(event)

        // when
        val teamMembersToNotify = eventRepository.getMembersThatHaveNotResponded(savedEvent.id)
        assert(teamMembersToNotify).isNotEmpty()
        assert(teamMembersToNotify).containsExactly(savedTeamMember1)
    }

    @Test
    fun `should return empty list when all team members have responded`() {
        // given
        val team = Team("", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "")
        val savedTeam = entityManager.persist(team)

        val user1 = Account("user1", "first", "last", "pass", "email1", "")
        val user2 = Account("user2", "second", "last", "pass", "email2", "")
        val savedUser1 = entityManager.persist(user1)
        val savedUser2 = entityManager.persist(user2)

        val teamMember1 = TeamMember(mutableSetOf(), savedTeam, "")
        val teamMember2 = TeamMember(mutableSetOf(), savedTeam, "")
        teamMember1.account = savedUser1
        teamMember2.account = savedUser2
        val savedTeamMember1 = entityManager.persist(teamMember1)
        val savedTeamMember2 = entityManager.persist(teamMember2)

        val place = Place("The stadium", "", "NYC", Place.PlaceType.STADIUM, savedTeam)
        val savedPlace = entityManager.persist(place)

        val event = Event.Builder()
                .name("event")
                .team(savedTeam)
                .fromDate(LocalDateTime.of(2019, 1, 1, 10, 0))
                .place(savedPlace)
                .build()
        event.presentMembers.add(savedTeamMember1)
        event.absentMembers.add(savedTeamMember2)
        val savedEvent = entityManager.persistAndFlush(event)

        // when
        val teamMembersToNotify = eventRepository.getMembersThatHaveNotResponded(savedEvent.id)
        assert(teamMembersToNotify).isEmpty()
    }
}