package com.zcorp.opensportmanagement.service

import assertk.assert
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.model.Account
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.model.TeamMember
import com.zcorp.opensportmanagement.repository.AccountRepository
import com.zcorp.opensportmanagement.repository.TeamMemberRepository
import com.zcorp.opensportmanagement.repository.TeamRepository
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.Optional

class AccountServiceTest {
    private val username = "foo"
    private val teamId = 5
    private val firstName = "firstname"
    private val lastName = "ln"
    private val password = "whatever"
    private val email = "this@camail.com"
    private val phoneNumber = "55965"
    private val mockUser = Account(firstName, lastName, password, email, phoneNumber)
    private val mockTeam = Team("SuperNam", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "", teamId)
    private val teamRepoMock: TeamRepository = mock()
    private val accountRepoMock: AccountRepository = mock()
    private val teamMemberRepoMock: TeamMemberRepository = mock()
    private val accountService: AccountService = AccountService(teamRepoMock, accountRepoMock, teamMemberRepoMock, BCryptPasswordEncoder())
    private val mockTeamMember = TeamMember(mutableSetOf(TeamMember.Role.PLAYER), mockTeam, mockUser)

    @Test
    fun `find user not existing should return null`() {
        whenever(accountRepoMock.findByUsername(any())).thenReturn(null)
        val user = accountService.findByUsername(username)
        assert(user).isNull()
    }

    @Test
    fun `find existing user should return the user`() {
        whenever(accountRepoMock.findByUsername(any())).thenReturn(mockUser)
        val user = accountService.findByUsername(username)
        assert(user?.firstName).isEqualTo(firstName)
        assert(user?.lastName).isEqualTo(lastName)
        assert(user?.email).isEqualTo(email)
        assert(user?.phoneNumber).isEqualTo(phoneNumber)
    }

    @Test
    fun `user that does not exist trying to join a team should not be possible`() {
        whenever(accountRepoMock.findByUsername(any())).thenReturn(null)
        assert {
            accountService.joinTeam(username, 1)
        }.thrownError { isInstanceOf(NotFoundException::class) }
    }

    @Test
    fun `user trying to join a team that does not exist should not be possible`() {
        whenever(accountRepoMock.findByUsername(username)).thenReturn(mockUser)
        whenever(teamRepoMock.findById(any())).thenReturn(Optional.empty())
        assert {
            accountService.joinTeam(username, 1)
        }.thrownError { isInstanceOf(NotFoundException::class) }
    }

    @Test
    fun `user trying to join a team should be possible`() {
        whenever(accountRepoMock.findByUsername(username)).thenReturn(mockUser)
        whenever(teamRepoMock.findById(any())).thenReturn(Optional.of(mockTeam))
        whenever(accountRepoMock.save(mockUser)).thenReturn(mockUser)
        whenever(teamMemberRepoMock.save(any<TeamMember>())).thenReturn(mockTeamMember)
        accountService.joinTeam(username, teamId)
        argumentCaptor<TeamMember>().apply {
            verify(teamMemberRepoMock, times(1)).save(capture())
            assert(allValues).hasSize(1)
            assert(firstValue).isEqualTo(mockTeamMember)
        }
    }
}
