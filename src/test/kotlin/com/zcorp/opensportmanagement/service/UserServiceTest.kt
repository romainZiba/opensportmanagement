package com.zcorp.opensportmanagement.service

import assertk.assertions.contains
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.model.User
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.repositories.UserRepository
import org.junit.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.Optional
import assertk.assert
import assertk.assertions.isInstanceOf

class UserServiceTest {
    private val teamId = 5
    private val username = "username"
    private val firstName = "firstname"
    private val lastName = "ln"
    private val password = "whatever"
    private val email = "this@camail.com"
    private val phoneNumber = "55965"
    private val mockUser = User(username, firstName, lastName, password, email, phoneNumber)
    private val mockTeam = Team("SuperNam", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "", teamId)
    private val teamRepoMock: TeamRepository = mock()
    private val userRepoMock: UserRepository = mock()
    private val userService: UserService = UserService(teamRepoMock, userRepoMock, BCryptPasswordEncoder())

    @Test
    fun `find user not existing should return null`() {
        whenever(userRepoMock.findByUsername(any())).thenReturn(null)
        val user = userService.findByUsername("Foo")
        assert(user).isNull()
    }

    @Test
    fun `find existing user should return the user`() {
        whenever(userRepoMock.findByUsername(mockUser.username)).thenReturn(mockUser)
        val user = userService.findByUsername(username)
        assert(user?.username).isEqualTo(username)
        assert(user?.firstName).isEqualTo(firstName)
        assert(user?.lastName).isEqualTo(lastName)
        assert(user?.email).isEqualTo(email)
        assert(user?.phoneNumber).isEqualTo(phoneNumber)
    }

    @Test
    fun `user that does not exist trying to join a team should not be possible`() {
        whenever(userRepoMock.findByUsername(any())).thenReturn(null)
        assert {
            userService.joinTeam("Foo", 1)
        }.thrownError { isInstanceOf(NotFoundException::class) }
    }

    @Test
    fun `user trying to join a team that does not exist should not be possible`() {
        whenever(userRepoMock.findByUsername(mockUser.username)).thenReturn(mockUser)
        whenever(teamRepoMock.findById(any())).thenReturn(Optional.empty())
        assert {
            userService.joinTeam(username, 1)
        }.thrownError { isInstanceOf(NotFoundException::class) }
    }

    @Test
    fun `user trying to join a team should be possible`() {
        whenever(userRepoMock.findByUsername(mockUser.username)).thenReturn(mockUser)
        whenever(teamRepoMock.findById(any())).thenReturn(Optional.of(mockTeam))
        assert(mockUser.getMemberOf()).hasSize(0)
        userService.joinTeam(username, teamId)
        assert(mockUser.getMemberOf()).hasSize(1)
        verify(userRepoMock, times(1)).save(mockUser)
    }
}
