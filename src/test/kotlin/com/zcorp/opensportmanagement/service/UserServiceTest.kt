package com.zcorp.opensportmanagement.service

import com.nhaarman.mockito_kotlin.*
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.model.User
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.repositories.UserRepository
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


class UserServiceTest: StringSpec() {
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

    override fun isInstancePerTest() = true

    init {
        "find user not existing should return null" {
            whenever(userRepoMock.findByUsername(any())).thenReturn(null)
            val user = userService.findByUsername("Foo")
            user shouldBe null
        }

        "find existing user should return the user" {
            whenever(userRepoMock.findByUsername(mockUser.username)).thenReturn(mockUser)
            val user = userService.findByUsername(username)
            user?.username shouldBe username
            user?.firstName shouldBe firstName
            user?.lastName shouldBe lastName
            user?.email shouldBe email
            user?.phoneNumber shouldBe phoneNumber
        }

        "user that does not exist trying to join a team should not be possible" {
            whenever(userRepoMock.findByUsername(any())).thenReturn(null)
            shouldThrow<NotFoundException> {
                userService.joinTeam("Foo", 1)
            }
        }

        "user trying to join a team that does not exist should not be possible" {
            whenever(userRepoMock.findByUsername(mockUser.username)).thenReturn(mockUser)
            whenever(teamRepoMock.getOne(any())).thenThrow(javax.persistence.EntityNotFoundException())
            shouldThrow<NotFoundException> {
                userService.joinTeam(username, 1)
            }
        }

        "user trying to join a team should be possible" {
            whenever(userRepoMock.findByUsername(mockUser.username)).thenReturn(mockUser)
            whenever(teamRepoMock.getOne(any())).thenReturn(mockTeam)
            mockUser.getMemberOf().size shouldBe 0
            userService.joinTeam(username, teamId)
            mockUser.getMemberOf().size shouldBe 1
            verify(userRepoMock, times(1)).save(mockUser)
        }
    }
}