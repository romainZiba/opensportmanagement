package com.zcorp.opensportmanagement.repository

import assertk.assert
import assertk.assertions.containsExactly
import assertk.assertions.isNotEmpty
import com.zcorp.opensportmanagement.model.Account
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.model.TeamMember
import com.zcorp.opensportmanagement.service.AccountService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@DataJpaTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(AccountService::class, BCryptPasswordEncoder::class)
open class TeamMemberRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var teamMemberRepo: TeamMemberRepository

    private lateinit var account1: Account
    private lateinit var account2: Account
    private lateinit var savedTeamMember1: TeamMember
    private lateinit var savedTeamMember2: TeamMember
    private lateinit var savedTeamMember3: TeamMember
    private lateinit var savedTeam1: Team
    private lateinit var savedTeam2: Team

    @BeforeEach
    fun init() {
        // Given a team
        val team1 = Team("1", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "")
        savedTeam1 = entityManager.persist(team1)
        val team2 = Team("2", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "")
        savedTeam2 = entityManager.persist(team2)

        // Given accounts
        account1 = Account("first", "last", "pass", "email1", "")
        account2 = Account("second", "last", "pass", "email2", "")
        val savedAccount1 = entityManager.persist(account1)
        val savedAccount2 = entityManager.persist(account2)

        // Given team members
        val teamMember1 = TeamMember(mutableSetOf(), savedTeam1, savedAccount1)
        val teamMember2 = TeamMember(mutableSetOf(), savedTeam1, savedAccount2)
        val teamMember3 = TeamMember(mutableSetOf(), savedTeam2, savedAccount1)

        savedTeamMember1 = entityManager.persist(teamMember1)
        savedTeamMember2 = entityManager.persist(teamMember2)
        savedTeamMember3 = entityManager.persist(teamMember3)
    }

    @Test
    fun `should return team members`() {
        val teamMembersAccount1 = teamMemberRepo.findMemberOfByUsername(account1.username)
        assert(teamMembersAccount1).isNotEmpty()
        assert(teamMembersAccount1).containsExactly(savedTeamMember1, savedTeamMember3)
        val teamMembersAccount2 = teamMemberRepo.findMemberOfByUsername(account2.username)
        assert(teamMembersAccount2).isNotEmpty()
        assert(teamMembersAccount2).containsExactly(savedTeamMember2)
    }
}