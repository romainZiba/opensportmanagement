package com.zcorp.opensportmanagement.service

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.repositories.*
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class TeamServiceTest: StringSpec() {
    private val teamRepoMock: TeamRepository = mock()
    private val userRepoMock: UserRepository = mock()
    private val seasonRepoMock: SeasonRepository = mock()
    private val opponentRepoMock: OpponentRepository = mock()
    private val teamMemberRepoMock: TeamMemberRepository = mock()
    private val teamService: TeamService = TeamService(teamRepoMock, teamMemberRepoMock, userRepoMock, seasonRepoMock, opponentRepoMock)
    private val teamIds = listOf(5, 8, 13 , 25)
    private val mockTeams = teamIds.map {
        Team("SuperName $it", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "", it)
    }

    override fun isInstancePerTest() = true

    init {
        "get teams should return the teams" {
            whenever(teamRepoMock.findByIds(teamIds)).thenReturn(mockTeams)
            val teams = teamService.getTeams(teamIds)
            teams shouldBe mockTeams.map { it.toDto() }
        }
    }
}