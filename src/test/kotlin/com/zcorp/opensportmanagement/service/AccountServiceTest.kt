package com.zcorp.opensportmanagement.service

import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.model.Account
import com.zcorp.opensportmanagement.repository.TeamRepository
import com.zcorp.opensportmanagement.repository.AccountRepository
import org.junit.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.Optional
import assertk.assert
import assertk.assertions.isInstanceOf

class AccountServiceTest {
    private val teamId = 5
    private val username = "username"
    private val firstName = "firstname"
    private val lastName = "ln"
    private val password = "whatever"
    private val email = "this@camail.com"
    private val phoneNumber = "55965"
    private val mockUser = Account(username, firstName, lastName, password, email, phoneNumber)
    private val mockTeam = Team("SuperNam", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "", teamId)
    private val teamRepoMock: TeamRepository = mock()
    private val accountRepoMock: AccountRepository = mock()
    private val accountService: AccountService = AccountService(teamRepoMock, accountRepoMock, BCryptPasswordEncoder())

    @Test
    fun `find user not existing should return null`() {
        whenever(accountRepoMock.findByUsername(any())).thenReturn(null)
        val user = accountService.findByUsername("Foo")
        assert(user).isNull()
    }

    @Test
    fun `find existing user should return the user`() {
        whenever(accountRepoMock.findByUsername(mockUser.username)).thenReturn(mockUser)
        val user = accountService.findByUsername(username)
        assert(user?.username).isEqualTo(username)
        assert(user?.firstName).isEqualTo(firstName)
        assert(user?.lastName).isEqualTo(lastName)
        assert(user?.email).isEqualTo(email)
        assert(user?.phoneNumber).isEqualTo(phoneNumber)
    }

    @Test
    fun `user that does not exist trying to join a team should not be possible`() {
        whenever(accountRepoMock.findByUsername(any())).thenReturn(null)
        assert {
            accountService.joinTeam("Foo", 1)
        }.thrownError { isInstanceOf(NotFoundException::class) }
    }

    @Test
    fun `user trying to join a team that does not exist should not be possible`() {
        whenever(accountRepoMock.findByUsername(mockUser.username)).thenReturn(mockUser)
        whenever(teamRepoMock.findById(any())).thenReturn(Optional.empty())
        assert {
            accountService.joinTeam(username, 1)
        }.thrownError { isInstanceOf(NotFoundException::class) }
    }

    @Test
    fun `user trying to join a team should be possible`() {
        whenever(accountRepoMock.findByUsername(mockUser.username)).thenReturn(mockUser)
        whenever(teamRepoMock.findById(any())).thenReturn(Optional.of(mockTeam))
        whenever(accountRepoMock.save(mockUser)).thenReturn(mockUser)
        assert(mockUser.getMemberOf()).hasSize(0)
        accountService.joinTeam(username, teamId)
        assert(mockUser.getMemberOf()).hasSize(1)
        verify(accountRepoMock, times(1)).save(mockUser)
    }
}
